package com.swx.dongzhou.pages.createPage

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.swx.dongzhou.Activities.CreateActivities.CreatePageConfigs
import com.swx.dongzhou.Activities.CreateActivities.FormCreateActivity
import com.swx.dongzhou.App
import com.swx.dongzhou.BaseFragment
import com.swx.dongzhou.R
import com.swx.dongzhou.Util.QRCodeType
import com.swx.dongzhou.databinding.CreateFragmentBinding
import androidx.core.content.edit
import com.swx.dongzhou.MainActivity

class CreateFragment: BaseFragment<CreateFragmentBinding>(CreateFragmentBinding::inflate) {

    val list = mutableListOf<CreateItem>()
    private var clipboardManager: ClipboardManager? = null
    private var clipboardText = ""
    private var clipboardMonitorStarted = false
    private val clipboardMonitor = ClipboardManager.OnPrimaryClipChangedListener {
        updateClipboardEntry()
    }

    override fun initView() {
        addItems()
        enableInsetsView(binding.createRoot,true,false)
        binding.RCView.layoutManager= GridLayoutManager(context,3)
        binding.RCView.adapter= CreateAdapter(activity,list)

        clipboardManager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboardText = getSavedClipboardText()
        renderClipboardEntry()
        startClipboardMonitor()
        updateClipboardEntry()
        scheduleClipboardRefresh()

        binding.layoutClipboardEntry.setOnClickListener {
            openTextCreateActivity()
        }
        binding.layoutHistoryEntry.setOnClickListener {
            (activity as MainActivity).showFragment("history")
        }
    }

    override fun loadData() {

    }

    override fun onResume() {
        super.onResume()
        startClipboardMonitor()
        updateClipboardEntry()
        scheduleClipboardRefresh()
    }

    override fun onPause() {
        stopClipboardMonitor()
        super.onPause()
    }

    fun addItems() {
        list.add(CreateItem(getString(R.string.website), QRCodeType.Website))
        list.add(CreateItem("WIFI", QRCodeType.WIFI))
        list.add(CreateItem(getString(R.string.text), QRCodeType.Text))
        list.add(CreateItem(getString(R.string.contact), QRCodeType.Contact))
        list.add(CreateItem(getString(R.string.tel), QRCodeType.Tel))
        list.add(CreateItem("E-mail", QRCodeType.Email))
        list.add(CreateItem(getString(R.string.sms), QRCodeType.SMS))
        list.add(CreateItem(getString(R.string.calendar), QRCodeType.Calendar))
        list.add(CreateItem(getString(R.string.mycard), QRCodeType.MyCard))
        list.add(CreateItem("FaceBook", QRCodeType.FaceBook))
        list.add(CreateItem("Instagram", QRCodeType.Instagram))
        list.add(CreateItem("WhatsApp", QRCodeType.WhatsApp))
        list.add(CreateItem("YouTube", QRCodeType.Youtube))
        list.add(CreateItem("Twitter", QRCodeType.Twitter))
        list.add(CreateItem("Spotify", QRCodeType.Spotify))
        list.add(CreateItem("PayPal", QRCodeType.Paypal))
        list.add(CreateItem("Viber", QRCodeType.Viber))
    }

    private fun updateClipboardEntry() {
        if (!isBindingAvailable) {
            return
        }
        val latestClipboardText = getClipboardText()
        if (latestClipboardText.isNotBlank()) {
            clipboardText = latestClipboardText
            saveClipboardText(latestClipboardText)
        }
        renderClipboardEntry()
    }

    private fun renderClipboardEntry() {
        if (!isBindingAvailable) {
            return
        }
        binding.textClipboardDesc.text = clipboardText
        binding.textClipboardDesc.visibility = if (clipboardText.isBlank()) View.GONE else View.VISIBLE
    }

    private fun openTextCreateActivity() {
        updateClipboardEntry()
        val intent = Intent(requireContext(), FormCreateActivity::class.java).apply {
            putExtra(CreatePageConfigs.EXTRA_CREATE_TYPE, QRCodeType.Text.name)
            putExtra(CreatePageConfigs.EXTRA_PREFILL_TEXT, clipboardText)
        }
        startActivity(intent)
    }

    private fun getClipboardText(): String {
        val clipData = clipboardManager?.primaryClip ?: return ""
        if (clipData.itemCount <= 0) {
            return ""
        }
        return clipData.getItemAt(0).coerceToText(requireContext())?.toString()?.trim().orEmpty()
    }

    private fun scheduleClipboardRefresh() {
        if (!isBindingAvailable) {
            return
        }
        binding.root.post {
            updateClipboardEntry()
        }
        binding.root.postDelayed({
            updateClipboardEntry()
        }, CLIPBOARD_REFRESH_DELAY)
    }

    private fun getSavedClipboardText(): String {
        return App.context
            .getSharedPreferences(CLIPBOARD_PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_CLIPBOARD_TEXT, "")
            .orEmpty()
    }

    private fun saveClipboardText(text: String) {
        App.context
            .getSharedPreferences(CLIPBOARD_PREFS_NAME, Context.MODE_PRIVATE)
            .edit {
                putString(KEY_CLIPBOARD_TEXT, text)
            }
    }

    private fun startClipboardMonitor() {
        if (clipboardMonitorStarted) {
            return
        }
        clipboardManager?.addPrimaryClipChangedListener(clipboardMonitor)
        clipboardMonitorStarted = true
    }

    private fun stopClipboardMonitor() {
        if (!clipboardMonitorStarted) {
            return
        }
        clipboardManager?.removePrimaryClipChangedListener(clipboardMonitor)
        clipboardMonitorStarted = false
    }

    override fun onDestroyView() {
        stopClipboardMonitor()
        clipboardManager = null
        super.onDestroyView()
    }

    companion object {
        private const val CLIPBOARD_PREFS_NAME = "clipboard_cache"
        private const val KEY_CLIPBOARD_TEXT = "key_clipboard_text"
        private const val CLIPBOARD_REFRESH_DELAY = 300L
    }
}
