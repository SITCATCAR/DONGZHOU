package com.swx.dongzhou.pages.historyPage

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.swx.dongzhou.Activities.ScanResultActivity
import com.swx.dongzhou.Activities.CreateActivities.CreatePageConfigs
import com.swx.dongzhou.App
import com.swx.dongzhou.BaseFragment
import com.swx.dongzhou.HistoryDatabase.History
import com.swx.dongzhou.HistoryDatabase.HistoryDatabase
import com.swx.dongzhou.R
import com.swx.dongzhou.Util.QRCodeType
import com.swx.dongzhou.Util.Utils
import com.swx.dongzhou.databinding.HistoryFragmentBinding
import com.swx.dongzhou.pages.historyPage.statemachine.HistoryContentMode
import com.swx.dongzhou.pages.historyPage.statemachine.HistoryNormalState
import com.swx.dongzhou.pages.historyPage.statemachine.HistorySelectionState
import com.swx.dongzhou.pages.historyPage.statemachine.HistoryStateMachine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HistoryFragment : BaseFragment<HistoryFragmentBinding>(
    HistoryFragmentBinding::inflate
) {
    lateinit var stateMachine: HistoryStateMachine
        private set

    private val historyList = mutableListOf<HistoryGroupItem>()
    private val selectedIds = mutableSetOf<Long>()
    private val selectedTypes = mutableSetOf<QRCodeType>()
    private val visibleIds = mutableSetOf<Long>()
    private val rawHistories = mutableListOf<History>()

    private lateinit var historyAdapter: HistoryAdapter
    private var filterPopupWindow: PopupWindow? = null
    private val scanResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            loadHistories()
        }
    }

    override fun initView() {
        initDarkModel()
        enableInsetsView(binding.historyRoot,true,false)
        historyAdapter = HistoryAdapter(
            itemList = historyList,
            onItemClick = ::onItemClick,
            onItemLongClick = ::onItemLongClick,
            onFavoriteClick = ::onFavoriteClick,
            onViewAllClick = ::onViewAllClick
        )
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.historyRecyclerView.adapter = historyAdapter

        stateMachine = HistoryStateMachine(this)
        stateMachine.changeState(HistoryNormalState())
        initAction()
        loadHistories()
    }

    private fun initDarkModel(){
        if(AppCompatDelegate.getDefaultNightMode()== AppCompatDelegate.MODE_NIGHT_YES){
            binding.imageBack.setImageResource(R.mipmap.ic_results_page_return_white)
        }
    }

    override fun loadData() {
    }

    fun clearSelection() {
        selectedIds.clear()
    }

    fun updateHistoryMode() {
        val isSelectionMode = stateMachine.currentState is HistorySelectionState
        val isFavoriteMode = getCurrentContentMode() == HistoryContentMode.FAVORITES
        binding.textHistoryTitle.visibility = if (isSelectionMode || isFavoriteMode) View.INVISIBLE else View.VISIBLE
        binding.textModeTitle.isVisible = isFavoriteMode && !isSelectionMode
        binding.imageBack.isVisible = isSelectionMode || isFavoriteMode
        binding.imageFilter.isVisible = !isSelectionMode && !isFavoriteMode
        binding.imageSelectAll.isVisible = isSelectionMode
        updateSelectAllIcon()
        applyHistoryList()
    }

    private fun initAction() {
        binding.imageFilter.setOnClickListener {
            showFilterPopup()
        }
        binding.imageBack.setOnClickListener {
            if (stateMachine.currentState is HistorySelectionState) {
                stateMachine.changeState(HistoryNormalState(getCurrentContentMode()))
            } else if (getCurrentContentMode() == HistoryContentMode.FAVORITES) {
                stateMachine.changeState(HistoryNormalState(HistoryContentMode.ALL))
            }
        }
        binding.imageSelectAll.setOnClickListener {
            toggleSelectAll()
        }
        binding.imageDelete.setOnClickListener {
            if (stateMachine.currentState is HistorySelectionState) {
                confirmDeleteSelected()
            } else {
                stateMachine.changeState(HistorySelectionState(getCurrentContentMode()))
            }
        }
    }

    private fun loadHistories() {
        if (!isBindingAvailable) {
            return
        }
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val histories = withContext(Dispatchers.IO) {
                    val dao = HistoryDatabase.getDatabase(App.context).HistoryDao()
                    if (selectedTypes.isEmpty()) {
                        dao.selectAll()
                    } else {
                        dao.selectByTypes(selectedTypes.toList())
                    }
                }
                rawHistories.clear()
                rawHistories.addAll(histories.sortedByDescending { history -> history.createdAt })
                applyHistoryList()
            } catch (e: Exception) {
                Log.e(TAG, "Load histories failed", e)
                if (!isHidden && isResumed) {
                    context?.let { safeContext ->
                        Toast.makeText(safeContext, "Load history failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun applyHistoryList() {
        if (!isBindingAvailable || !::historyAdapter.isInitialized) {
            return
        }
        visibleIds.clear()
        val isFavoriteMode = getCurrentContentMode() == HistoryContentMode.FAVORITES
        val displayHistories = if (isFavoriteMode) {
            rawHistories.filter { history -> history.isFavorite }
        } else {
            rawHistories
        }
        displayHistories.forEach { history ->
            visibleIds.add(history.id)
        }

        val groups = mutableListOf<HistoryGroupItem>()
        val favoriteItems = rawHistories
            .filter { history -> history.isFavorite }
            .sortedByDescending { history -> history.favoriteAt ?: history.createdAt }
        if (!isFavoriteMode && favoriteItems.isNotEmpty()) {
            groups.add(
                HistoryGroupItem(
                    title = "Favorites",
                    records = favoriteItems.take(3).map { history ->
                        history.toRecordItem()
                    },
                    showViewAll = favoriteItems.size > 3
                )
            )
        }

        displayHistories
            .groupBy { history -> getGroupTitle(history.createdAt) }
            .forEach { entry ->
                groups.add(
                    HistoryGroupItem(
                        title = entry.key,
                        records = entry.value.map { history ->
                            history.toRecordItem()
                        }
                    )
                )
            }

        historyAdapter.setItems(groups)
        updateEmptyHistory(displayHistories.isEmpty())
        updateSelectAllIcon()
    }

    private fun updateEmptyHistory(isEmpty: Boolean) {
        binding.layoutEmptyHistory.isVisible = isEmpty
        binding.historyRecyclerView.isVisible = !isEmpty
    }

    private fun History.toRecordItem(): HistoryRecordItem {
        return HistoryRecordItem(
            id = id,
            title = title,
            content = content,
            type = getTypeName(type),
            qrCodeType = type,
            iconRes = Utils.getItemImage(type),
            time = formatTime(createdAt),
            isFavorite = isFavorite,
            showFavoriteIcon = true,
            isSelected = selectedIds.contains(id),
            isSelectionMode = stateMachine.currentState is HistorySelectionState
        )
    }

    private fun onItemClick(item: HistoryRecordItem) {
        if (stateMachine.currentState is HistorySelectionState) {
            toggleSelection(item.id)
        } else {
            val intent = Intent(requireContext(), ScanResultActivity::class.java).apply {
                putExtra(ScanResultActivity.EXTRA_SCAN_RESULT, item.content)
                putExtra(ScanResultActivity.EXTRA_SCAN_TYPE, item.qrCodeType.name)
                putExtra(ScanResultActivity.EXTRA_HISTORY_ID, item.id)
            }
            scanResultLauncher.launch(intent)
        }
    }

    private fun onItemLongClick(item: HistoryRecordItem) {
        if (stateMachine.currentState !is HistorySelectionState) {
            selectedIds.clear()
            selectedIds.add(item.id)
            stateMachine.changeState(HistorySelectionState(getCurrentContentMode()))
        }
    }

    private fun onFavoriteClick(item: HistoryRecordItem) {
        if (stateMachine.currentState is HistorySelectionState) {
            return
        }
        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    HistoryDatabase.getDatabase(App.context).HistoryDao().updateFavorite(
                        id = item.id,
                        isFavorite = !item.isFavorite,
                        favoriteAt = if (item.isFavorite) null else System.currentTimeMillis()
                    )
                }
                loadHistories()
            } catch (e: Exception) {
                Toast.makeText(App.context, "Update favorite failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onViewAllClick() {
        stateMachine.changeState(HistoryNormalState(HistoryContentMode.FAVORITES))
        binding.historyRecyclerView.scrollToPosition(0)
    }

    private fun toggleSelection(id: Long) {
        if (selectedIds.contains(id)) {
            selectedIds.remove(id)
        } else {
            selectedIds.add(id)
        }
        applyHistoryList()
    }

    private fun toggleSelectAll() {
        if (visibleIds.isEmpty()) {
            return
        }
        if (selectedIds.containsAll(visibleIds)) {
            selectedIds.clear()
        } else {
            selectedIds.clear()
            selectedIds.addAll(visibleIds)
        }
        applyHistoryList()
    }

    private fun updateSelectAllIcon() {
        if (!isBindingAvailable) {
            return
        }
        val isAllSelected = visibleIds.isNotEmpty() && selectedIds.containsAll(visibleIds)
        binding.imageSelectAll.setImageResource(if (isAllSelected) R.mipmap.ic_select_all else R.mipmap.ic_select)
    }

    private fun confirmDeleteSelected() {
        if (selectedIds.isEmpty()) {
            context?.let { safeContext ->
                Toast.makeText(safeContext, "Please select history", Toast.LENGTH_SHORT).show()
            }
            return
        }
        val dialogView = layoutInflater.inflate(R.layout.history_delete_dialog, null)
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(dialogView)

        dialogView.findViewById<TextView>(R.id.text_dialog_message).text =
            "Are you sure want to clear\n${selectedIds.size} selected history?"
        dialogView.findViewById<TextView>(R.id.text_dialog_yes).setOnClickListener {
            dialog.dismiss()
            deleteSelected()
        }
        dialogView.findViewById<TextView>(R.id.text_dialog_no).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(resources.displayMetrics.widthPixels - dp(40), ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun deleteSelected() {
        val ids = selectedIds.toList()
        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    HistoryDatabase.getDatabase(App.context).HistoryDao().deleteByIds(ids)
                }
                stateMachine.changeState(HistoryNormalState(getCurrentContentMode()))
                loadHistories()
            } catch (e: Exception) {
                Toast.makeText(App.context, "Delete history failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showFilterPopup() {
        val context = requireContext()
        val popupContent = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            background = context.getDrawable(R.drawable.bg_card_16)
            setPadding(0, dp(10), 0, dp(10))
        }

        QRCodeType.values().forEach { type ->
            val itemView = LayoutInflater.from(context)
                .inflate(R.layout.history_filter_item, popupContent, false)
            bindFilterItem(itemView, type)
            popupContent.addView(itemView)
        }

        filterPopupWindow = PopupWindow(
            popupContent,
            dp(210),
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            isOutsideTouchable = true
            elevation = dp(8).toFloat()
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            showAsDropDown(binding.imageFilter, -dp(190), 0)
        }
    }

    private fun bindFilterItem(itemView: View, type: QRCodeType) {
        val icon = itemView.findViewById<ImageView>(R.id.image_filter_icon)
        val name = itemView.findViewById<TextView>(R.id.text_filter_name)
        val select = itemView.findViewById<ImageView>(R.id.image_filter_select)

        icon.setImageResource(Utils.getItemImage(type))
        name.text = getTypeName(type)
        updateFilterSelectIcon(select, type)
        itemView.setOnClickListener {
            if (selectedTypes.contains(type)) {
                selectedTypes.remove(type)
            } else {
                selectedTypes.add(type)
            }
            updateFilterSelectIcon(select, type)
            if (stateMachine.currentState is HistorySelectionState) {
                stateMachine.changeState(HistoryNormalState(getCurrentContentMode()))
            }
            loadHistories()
        }
    }

    private fun updateFilterSelectIcon(select: ImageView, type: QRCodeType) {
        select.setImageResource(if (selectedTypes.contains(type)) R.mipmap.ic_select_all else R.mipmap.ic_select)
    }

    private fun getGroupTitle(timestamp: Long): String {
        val today = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            timeInMillis = timestamp
        }
        val isToday = today.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
            today.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR)
        if (isToday) {
            return "Today"
        }
        return SimpleDateFormat("MMM d, yyyy", Locale.US).format(timestamp)
    }

    private fun formatTime(timestamp: Long): String {
        return SimpleDateFormat("HH:mm", Locale.US).format(timestamp)
    }

    private fun getTypeName(type: QRCodeType): String {
        return CreatePageConfigs.getConfig(type).title
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    private fun getCurrentContentMode(): HistoryContentMode {
        return stateMachine.currentState?.contentMode ?: HistoryContentMode.ALL
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            loadHistories()
        } else if (::stateMachine.isInitialized) {
            stateMachine.changeState(HistoryNormalState())
        }
    }

    override fun onDestroyView() {
        filterPopupWindow?.dismiss()
        filterPopupWindow = null
        super.onDestroyView()
    }

    companion object {
        private const val TAG = "HistoryFragment"
    }
}
