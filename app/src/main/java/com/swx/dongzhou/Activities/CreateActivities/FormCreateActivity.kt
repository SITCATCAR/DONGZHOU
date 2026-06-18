package com.swx.dongzhou.Activities.CreateActivities

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import com.swx.dongzhou.BaseActivity
import com.swx.dongzhou.databinding.ActivityFormCreateBinding
import com.swx.dongzhou.pages.createPage.CreateItemType

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

    override fun initData() {
        val typeName = intent.getStringExtra(CreatePageConfigs.EXTRA_CREATE_TYPE)
        val type = runCatching {
            CreateItemType.valueOf(typeName.orEmpty())
        }.getOrDefault(CreateItemType.Website)

        config = CreatePageConfigs.getConfig(type)
    }

    override fun initView() {
        enableInsetsView(binding.root, top = true, bottom = true)
        binding.tvTitle.text = config.title
        initViewMaps()
        initVisibleFields()
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
            if (config.type == CreateItemType.Email) {
                toggleEmailExtraContent()
            }
        }

        binding.etEmail.setOnFocusChangeListener { _, hasFocus ->
            if (config.type == CreateItemType.Email && hasFocus && !emailExpanded) {
                toggleEmailExtraContent()
            }
        }

        binding.layoutSecurity.setOnClickListener {
            securityDropdownVisible = !securityDropdownVisible
            binding.layoutSecurityDropdown.visibility =
                if (securityDropdownVisible) View.VISIBLE else View.GONE
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
            if (config.type == CreateItemType.Calendar) {
                showCalendarLocation()
                binding.etTitle.requestFocus()
            }
        }

        binding.etTitle.setOnFocusChangeListener { _, hasFocus ->
            if (config.type == CreateItemType.Calendar && hasFocus) {
                showCalendarLocation()
            }
        }

        binding.switchAllDay.setOnCheckedChangeListener { _, checked ->
            binding.tvStartTime.text = if (checked) "Jan 8" else "Jan 8 9:30"
            binding.tvEndTime.text = if (checked) "Jan 8" else "11:30"
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
            handleCreate()
        }
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
        binding.layoutSecurityDropdown.visibility = View.GONE

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
        binding.layoutPassword.visibility = if (type == "None") View.GONE else View.VISIBLE
    }

    private fun showCalendarLocation() {
        setFieldVisibility("location", View.VISIBLE)
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
        //TODO 创建二维码
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show()
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
}
