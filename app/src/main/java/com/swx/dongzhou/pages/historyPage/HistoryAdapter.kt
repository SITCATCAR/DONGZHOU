package com.swx.dongzhou.pages.historyPage

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.swx.dongzhou.R

class HistoryAdapter(
    private val itemList: MutableList<HistoryGroupItem>,
    private val onItemClick: (HistoryRecordItem) -> Unit,
    private val onItemLongClick: (HistoryRecordItem) -> Unit,
    private val onFavoriteClick: (HistoryRecordItem) -> Unit,
    private val onViewAllClick: () -> Unit
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.history_group_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val group = itemList[position]
        holder.groupTitle.text = group.title
        holder.recordContainer.removeAllViews()

        group.records.forEachIndexed { index, item ->
            val recordView = LayoutInflater.from(holder.itemView.context)
                .inflate(R.layout.history_record_item, holder.recordContainer, false)
            bindRecord(recordView, item)
            addRecordMargin(recordView, index)
            holder.recordContainer.addView(recordView)
        }

        if (group.showViewAll) {
            val viewAllView = LayoutInflater.from(holder.itemView.context)
                .inflate(R.layout.history_view_all_item, holder.recordContainer, false)
            addViewAllMargin(viewAllView)
            viewAllView.setOnClickListener {
                onViewAllClick()
            }
            holder.recordContainer.addView(viewAllView)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun setItems(list: List<HistoryGroupItem>) {
        itemList.clear()
        itemList.addAll(list)
        notifyDataSetChanged()
    }

    private fun bindRecord(view: View, item: HistoryRecordItem) {
        val recordIcon = view.findViewById<ImageView>(R.id.image_record_icon)
        val recordTitle = view.findViewById<TextView>(R.id.text_record_title)
        val recordType = view.findViewById<TextView>(R.id.text_record_type)
        val recordTime = view.findViewById<TextView>(R.id.text_record_time)
        val favoriteIcon = view.findViewById<ImageView>(R.id.image_record_favorite)
        val selectIcon = view.findViewById<ImageView>(R.id.image_record_select)

        recordIcon.setImageResource(item.iconRes)
        recordTitle.text = item.title
        recordType.text = item.type
        recordTime.text = item.time
        recordTime.isVisible = item.time != null && !item.isSelectionMode

        favoriteIcon.isVisible = !item.isSelectionMode
        favoriteIcon.setImageResource(
            if (item.isFavorite) {
                R.mipmap.ic_favorites_selected
            } else {
                R.drawable.ic_favorites_unselected
            }
        )
        favoriteIcon.setOnClickListener {
            onFavoriteClick(item)
        }

        selectIcon.isVisible = item.isSelectionMode
        selectIcon.setImageResource(if (item.isSelected) R.mipmap.ic_select_all else R.mipmap.ic_select)

        view.setOnClickListener {
            onItemClick(item)
        }
        view.setOnLongClickListener {
            onItemLongClick(item)
            true
        }
    }

    private fun addRecordMargin(view: View, index: Int) {
        if (index == 0) {
            return
        }
        val params = view.layoutParams as LinearLayout.LayoutParams
        params.topMargin = view.resources.getDimensionPixelSize(R.dimen.history_record_margin_top)
        view.layoutParams = params
    }

    private fun addViewAllMargin(view: View) {
        val params = view.layoutParams as LinearLayout.LayoutParams
        params.topMargin = view.resources.getDimensionPixelSize(R.dimen.history_view_all_margin_top)
        params.gravity = Gravity.END
        view.layoutParams = params
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val groupTitle: TextView = view.findViewById(R.id.text_group_title)
        val recordContainer: LinearLayout = view.findViewById(R.id.layout_record_container)
    }
}
