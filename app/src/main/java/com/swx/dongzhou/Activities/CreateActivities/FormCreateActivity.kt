package com.swx.dongzhou.Activities.CreateActivities

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.lifecycleScope
import com.swx.dongzhou.Activities.CreateResultActivity
import com.swx.dongzhou.BaseActivity
import com.swx.dongzhou.HistoryDatabase.History
import com.swx.dongzhou.HistoryDatabase.HistoryDatabase
import com.swx.dongzhou.R
import com.swx.dongzhou.Util.QRCodeTextCodec
import com.swx.dongzhou.Util.QRCodeType
import com.swx.dongzhou.databinding.ActivityFormCreateBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FormCreateActivity : BaseActivity<ActivityFormCreateBinding>(
    ActivityFormCreateBinding::inflate
) {
    private lateinit var config: CreatePageConfig
    private var emailExpanded = false
    private var securityDropdownVisible = false

    private lateinit var fieldViews: Map<String, View>
    private lateinit var labelViews: Map<String, View>
    private lateinit var editTexts: Map<String, EditText>
    private lateinit var textValues: Map<String, TextView>
    private lateinit var switches: Map<String, SwitchCompat>
    private var securityPopupWindow: PopupWindow? = null
    private var prefillText = ""
    private var clipboardTipText = ""

    override fun initData() {
        val typeName = intent.getStringExtra(CreatePageConfigs.EXTRA_CREATE_TYPE)
        val type = runCatching {
            QRCodeType.valueOf(typeName.orEmpty())
        }.getOrDefault(QRCodeType.Website)

        config = CreatePageConfigs.getConfig(type)
        //接收传入的粘贴板内容
        prefillText = intent.getStringExtra(CreatePageConfigs.EXTRA_PREFILL_TEXT).orEmpty()
        if (type == QRCodeType.Text) {
            clipboardTipText = prefillText.ifBlank { getClipboardText() }
        }
    }

    override fun initView() {
        enableInsetsView(binding.root, left = true, top = true, right = true, bottom = false)
        //无视ime insets
        enableInsetsView(binding.layoutCreateButton, left = false, top = false, right = false, bottom = true, includeIme = false)
        initDarkModel()
        binding.tvTitle.text = config.title
        initViewMaps()
        initVisibleFields()
    }

    //返回箭头是图片，dark模式只能更换图片。
    private fun initDarkModel(){
        if(AppCompatDelegate.getDefaultNightMode()== AppCompatDelegate.MODE_NIGHT_YES){
            binding.btnBack.setImageResource(R.mipmap.ic_results_page_return_white)
        }
    }

    override fun initAction() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.tvPrefixWww.setOnClickListener {
            addPrefix(binding.etUrl, "www.")
        }

        binding.tvSuffixCom.setOnClickListener {
            addSuffix(binding.etUrl, ".com")
        }

        binding.layoutEmail.setOnClickListener {
            if (config.type == QRCodeType.Email) {
                toggleEmailExtraContent()
            }
        }

        binding.etEmail.setOnFocusChangeListener { _, hasFocus ->
            if (config.type == QRCodeType.Email && hasFocus && !emailExpanded) {
                toggleEmailExtraContent()
            }
        }

        binding.layoutSecurity.setOnClickListener {
            showSecurityPopup()
        }

        binding.tvSecurityWpa.setOnClickListener {
            selectSecurityType("WPA/WPA2")
        }

        binding.tvSecurityWep.setOnClickListener {
            selectSecurityType("WEP")
        }

        binding.tvSecurityNone.setOnClickListener {
            selectSecurityType("None")
        }

        binding.etText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tvTextCount.text = (s?.length ?: 0).toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.layoutTitle.setOnClickListener {
            if (config.type == QRCodeType.Calendar) {
                showCalendarLocation()
                binding.etTitle.requestFocus()
            }
        }

        binding.etTitle.setOnFocusChangeListener { _, hasFocus ->
            if (config.type == QRCodeType.Calendar && hasFocus) {
                showCalendarLocation()
            }
        }

        binding.switchAllDay.setOnCheckedChangeListener { _, checked ->
            binding.tvStartTime.text = if (checked) "Jan 8" else "Jan 8 9:30"
            binding.tvEndTime.text = if (checked) "Jan 8" else "11:30"
            updateCreateButtonState()
        }

        binding.layoutStartTime.setOnClickListener {
            Toast.makeText(this, "Select start", Toast.LENGTH_SHORT).show()
        }

        binding.layoutEndTime.setOnClickListener {
            Toast.makeText(this, "Select end", Toast.LENGTH_SHORT).show()
        }

        binding.layoutBirthday.setOnClickListener {
            Toast.makeText(this, "Select birthday", Toast.LENGTH_SHORT).show()
        }

        binding.btnCreate.setOnClickListener {
            if (!isCreateEnabled()) {
                return@setOnClickListener
            }
            handleCreate()
        }

        binding.layoutClipboardTip.setOnClickListener {
            appendClipboardTipToInput()
        }

        applyClipboardContent()
        setupCreateButtonState()
    }

    private fun applyClipboardContent() {
        if (config.type != QRCodeType.Text) {
            return
        }
        if (prefillText.isNotBlank()) {
            binding.etText.setText(prefillText)
            binding.etText.setSelection(binding.etText.text.length)
        }
        updateClipboardTip()
        binding.root.postDelayed({
            val latestClipboardText = getClipboardText()
            if (latestClipboardText.isNotBlank()) {
                clipboardTipText = latestClipboardText
                updateClipboardTip()
            }
        }, CLIPBOARD_REFRESH_DELAY)
    }

    private fun updateClipboardTip() {
        val tipText = clipboardTipText.ifBlank { prefillText }
        if (tipText.isBlank()) {
            binding.layoutClipboardTip.visibility = View.GONE
            return
        }
        binding.tvClipboardTip.text = tipText
        binding.layoutClipboardTip.visibility = View.VISIBLE
    }

    private fun appendClipboardTipToInput() {
        val tipText = binding.tvClipboardTip.text?.toString().orEmpty()
        if (tipText.isBlank()) {
            return
        }
        binding.etText.append(tipText)
        binding.etText.setSelection(binding.etText.text.length)
    }

    private fun initViewMaps() {
        fieldViews = mapOf(
            "url" to binding.layoutUrl,
            "wifiName" to binding.layoutWifiName,
            "security" to binding.layoutSecurity,
            "password" to binding.layoutPassword,
            "text" to binding.layoutText,
            "name" to binding.layoutName,
            "phone1" to binding.layoutPhone1,
            "phone2" to binding.layoutPhone2,
            "phone" to binding.layoutPhone,
            "email" to binding.layoutEmail,
            "subject" to binding.layoutSubject,
            "content" to binding.layoutContent,
            "message" to binding.layoutMessage,
            "title" to binding.layoutTitle,
            "location" to binding.layoutLocation,
            "allDay" to binding.layoutAllDay,
            "startTime" to binding.layoutStartTime,
            "endTime" to binding.layoutEndTime,
            "description" to binding.layoutDescription,
            "address" to binding.layoutAddress,
            "birthday" to binding.layoutBirthday,
            "org" to binding.layoutOrg,
            "note" to binding.layoutNote
        )

        labelViews = mapOf(
            "subject" to binding.tvSubjectLabel,
            "content" to binding.tvContentLabel,
            "message" to binding.tvMessageLabel,
            "title" to binding.tvTitleLabel,
            "location" to binding.tvLocationLabel,
            "description" to binding.tvDescriptionLabel,
            "note" to binding.tvNoteLabel
        )

        editTexts = mapOf(
            "url" to binding.etUrl,
            "wifiName" to binding.etWifiName,
            "password" to binding.etPassword,
            "text" to binding.etText,
            "name" to binding.etName,
            "phone1" to binding.etPhone1,
            "phone2" to binding.etPhone2,
            "phone" to binding.etPhone,
            "email" to binding.etEmail,
            "subject" to binding.etSubject,
            "content" to binding.etContent,
            "message" to binding.etMessage,
            "title" to binding.etTitle,
            "location" to binding.etLocation,
            "description" to binding.etDescription,
            "address" to binding.etAddress,
            "org" to binding.etOrg,
            "note" to binding.etNote
        )

        textValues = mapOf(
            "security" to binding.tvSecurity,
            "startTime" to binding.tvStartTime,
            "endTime" to binding.tvEndTime,
            "birthday" to binding.tvBirthday
        )

        switches = mapOf(
            "allDay" to binding.switchAllDay
        )
    }

    private fun initVisibleFields() {
        fieldViews.values.forEach { view ->
            view.visibility = View.GONE
        }
        labelViews.values.forEach { view ->
            view.visibility = View.GONE
        }
        binding.layoutQuickText.visibility = if (config.showQuickText) View.VISIBLE else View.GONE
        binding.layoutClipboardTip.visibility = View.GONE
        binding.layoutSecurityDropdown.visibility = View.GONE
        securityDropdownVisible = false

        config.fields.forEach { field ->
            setFieldVisibility(field.key, if (field.visibleOnStart) View.VISIBLE else View.GONE)
            applyFieldDefaultValue(field)
        }
    }

    private fun applyFieldDefaultValue(field: CreateFieldConfig) {
        if (field.defaultValue.isNotBlank()) {
            textValues[field.key]?.text = field.defaultValue
            switches[field.key]?.isChecked = field.defaultValue == "true"
        }
    }

    private fun setFieldVisibility(key: String, visibility: Int) {
        fieldViews[key]?.visibility = visibility
        labelViews[key]?.visibility = visibility
    }

    private fun toggleEmailExtraContent() {
        emailExpanded = !emailExpanded
        val visibility = if (emailExpanded) View.VISIBLE else View.GONE
        setFieldVisibility("subject", visibility)
        setFieldVisibility("content", visibility)
    }

    private fun selectSecurityType(type: String) {
        binding.tvSecurity.text = type
        binding.layoutSecurityDropdown.visibility = View.GONE
        securityDropdownVisible = false
        securityPopupWindow?.dismiss()
        binding.layoutPassword.visibility = if (type == "None") View.GONE else View.VISIBLE
        updateCreateButtonState()
    }

    private fun showSecurityPopup() {
        if (config.type != QRCodeType.WIFI) {
            return
        }
        val popupContent = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = getDrawable(R.drawable.bg_card_16)
            setPadding(0, dp(10), 0, dp(10))
        }

        listOf("WPA/WPA2", "WEP", "None").forEach { option ->
            val itemView = LayoutInflater.from(this)
                .inflate(R.layout.create_security_popup_item, popupContent, false) as TextView
            itemView.text = option
            itemView.setOnClickListener {
                selectSecurityType(option)
            }
            popupContent.addView(itemView)
        }

        securityPopupWindow?.dismiss()
        val popupStartOffset = dp(64)
        val popupWidth = (binding.layoutSecurity.width - dp(96)).coerceAtLeast(dp(240))
        securityPopupWindow = PopupWindow(
            popupContent,
            popupWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            isOutsideTouchable = true
            elevation = dp(8).toFloat()
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            showAsDropDown(binding.layoutSecurity, popupStartOffset, 0)
        }
    }

    private fun showCalendarLocation() {
        setFieldVisibility("location", View.VISIBLE)
    }

    private fun setupCreateButtonState() {
        editTexts.values.forEach { editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    updateCreateButtonState()
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })
        }
        updateCreateButtonState()
    }

    private fun updateCreateButtonState() {
        val canCreate = isCreateEnabled()
        binding.btnCreate.isEnabled = canCreate
    }

    private fun isCreateEnabled(): Boolean {
        val values = collectValues()
        return config.fields
            .filter { field -> field.required }
            .all { field -> values[field.key].orEmpty().isNotBlank() }
    }

    private fun handleCreate() {
        val values = collectValues()
        val switchValues = switches.mapValues { entry -> entry.value.isChecked }

        val missingField = config.fields.firstOrNull { field ->
            field.required && values[field.key].orEmpty().isBlank()
        }
        if (missingField != null) {
            Toast.makeText(this, "Please enter ${missingField.hint.ifBlank { missingField.label.orEmpty() }}", Toast.LENGTH_SHORT).show()
            return
        }

        val content = CreatePageConfigs.createContent(
            type = config.type,
            selectedTab = "",
            values = values,
            switches = switchValues
        )
        if (QRCodeTextCodec.createQRCodeContent(content, config.type) == null) {
            Toast.makeText(this, "Content is too long to create QR code", Toast.LENGTH_SHORT).show()
            return
        }
        val title = getHistoryTitle(values, content)
        saveHistoryAndOpenResult(title, content)
    }

    private fun saveHistoryAndOpenResult(title: String, content: String) {
        lifecycleScope.launch {
            try {
                val historyId = withContext(Dispatchers.IO) {
                    val history = History(
                        title = title,
                        content = content,
                        type = config.type
                    )
                    HistoryDatabase.getDatabase(this@FormCreateActivity).HistoryDao().insert(history)
                }
                val intent = Intent(this@FormCreateActivity, CreateResultActivity::class.java).apply {
                    putExtra("type", config.type.name)
                    putExtra("content", content)
                    putExtra("historyId", historyId)
                }
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this@FormCreateActivity, "Save history failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getHistoryTitle(values: Map<String, String>, content: String): String {
        val titleKey = config.fields.firstOrNull { field -> field.required }?.key
            ?: config.fields.firstOrNull()?.key
        return values[titleKey].orEmpty().ifBlank { content }
    }

    private fun collectValues(): Map<String, String> {
        val values = mutableMapOf<String, String>()
        editTexts.forEach { entry ->
            values[entry.key] = entry.value.text.toString().trim()
        }
        textValues.forEach { entry ->
            values[entry.key] = entry.value.text.toString().trim()
        }
        return values
    }

    private fun addPrefix(editText: EditText, prefix: String) {
        val currentText = editText.text.toString()
        if (currentText.startsWith(prefix)) {
            editText.setSelection(editText.text.length)
            return
        }

        editText.setText(prefix + currentText)
        editText.setSelection(editText.text.length)
    }

    private fun addSuffix(editText: EditText, suffix: String) {
        val currentText = editText.text.toString()
        if (currentText.endsWith(suffix)) {
            editText.setSelection(editText.text.length)
            return
        }

        editText.setText(currentText + suffix)
        editText.setSelection(editText.text.length)
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    private fun getClipboardText(): String {
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = clipboardManager.primaryClip ?: return ""
        if (clipData.itemCount <= 0) {
            return ""
        }
        return clipData.getItemAt(0).coerceToText(this)?.toString()?.trim().orEmpty()
    }

    override fun onDestroy() {
        securityPopupWindow?.dismiss()
        securityPopupWindow = null
        super.onDestroy()
    }

    companion object {
        private const val CLIPBOARD_REFRESH_DELAY = 300L
    }
}
