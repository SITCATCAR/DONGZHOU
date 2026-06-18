package com.swx.dongzhou.Activities.CreateActivities

import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import com.swx.dongzhou.BaseActivity
import com.swx.dongzhou.R
import com.swx.dongzhou.databinding.ActivityCommonCreateBinding
import com.swx.dongzhou.pages.createPage.CreateItemType

class CommonCreateActivity : BaseActivity<ActivityCommonCreateBinding>(
    ActivityCommonCreateBinding::inflate
) {
    private lateinit var config: CreatePageConfig
    private var selectedTab = ""
    private var emailExpanded = false
    private var securityDropdownVisible = false

    private val editTexts = mutableMapOf<String, EditText>()
    private val textValues = mutableMapOf<String, TextView>()
    private val switches = mutableMapOf<String, SwitchCompat>()
    private val fieldViews = mutableMapOf<String, View>()
    private val dropdownViews = mutableMapOf<String, View>()

    override fun initData() {
        val typeName = intent.getStringExtra(CreatePageConfigs.EXTRA_CREATE_TYPE)
        val type = runCatching {
            CreateItemType.valueOf(typeName.orEmpty())
        }.getOrDefault(CreateItemType.Website)

        config = CreatePageConfigs.getConfig(type)
        selectedTab = config.tabs.firstOrNull().orEmpty()
    }

    override fun initView() {
        enableInsetsView(binding.root, top = true, bottom = true)
        binding.tvTitle.text = config.title

        if (config.mode == CreatePageMode.APP) {
            initAppView()
        } else {
            initFormView()
        }
    }

    override fun initAction() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnCreate.setOnClickListener {
            handleCreate()
        }
    }

    private fun initAppView() {
        binding.layoutAppCard.visibility = View.VISIBLE
        binding.layoutForm.visibility = View.GONE
        binding.ivAppIcon.setImageResource(config.iconRes)
        binding.tvOpenTag.visibility = if (config.showOpenTag) View.VISIBLE else View.GONE
        binding.tvCountryCode.visibility = if (config.showCountryCode) View.VISIBLE else View.GONE
        binding.viewCountryDivider.visibility = if (config.showCountryCode) View.VISIBLE else View.GONE
        binding.layoutQuickText.visibility = if (config.showQuickText) View.VISIBLE else View.GONE

        initTabs()
        updateAppHint()

        binding.tvPrefixWww.setOnClickListener {
            addPrefix(binding.etAppInput, "www.")
        }

        binding.tvSuffixCom.setOnClickListener {
            addSuffix(binding.etAppInput, ".com")
        }
    }

    private fun initTabs() {
        binding.layoutTabs.removeAllViews()
        binding.layoutTabs.visibility = if (config.tabs.isEmpty()) View.GONE else View.VISIBLE

        config.tabs.forEach { tab ->
            val tabView = TextView(this).apply {
                text = tab
                gravity = Gravity.CENTER
                textSize = 15f
                setOnClickListener {
                    selectedTab = tab
                    updateTabs()
                    updateAppHint()
                }
            }
            binding.layoutTabs.addView(
                tabView,
                LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f)
            )
        }

        updateTabs()
    }

    private fun updateTabs() {
        for (index in 0 until binding.layoutTabs.childCount) {
            val tabView = binding.layoutTabs.getChildAt(index) as TextView
            val selected = tabView.text.toString() == selectedTab
            tabView.setTextColor(color(if (selected) R.color.white else R.color.gray_text))
            tabView.setTypeface(null, if (selected) android.graphics.Typeface.BOLD else android.graphics.Typeface.NORMAL)
            tabView.background = if (selected) {
                ContextCompat.getDrawable(this, R.drawable.bg_tab_selected)
            } else {
                null
            }
        }
    }

    private fun updateAppHint() {
        binding.etAppInput.hint = config.tabHints[selectedTab] ?: config.appHint
    }

    private fun initFormView() {
        binding.layoutAppCard.visibility = View.GONE
        binding.layoutForm.visibility = View.VISIBLE
        binding.layoutForm.removeAllViews()

        config.fields.forEach { field ->
            val fieldView = createFieldView(field)
            fieldView.visibility = if (field.visibleOnStart) View.VISIBLE else View.GONE
            fieldViews[field.key] = fieldView
            binding.layoutForm.addView(fieldView)
        }

        if (config.showQuickText) {
            binding.layoutForm.addView(createQuickTextRow(editTexts["url"]))
        }

        initFormSpecialActions()
    }

    private fun initFormSpecialActions() {
        if (config.type == CreateItemType.Email) {
            fieldViews["email"]?.setOnClickListener {
                toggleEmailExtraContent()
            }
            editTexts["email"]?.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus && !emailExpanded) {
                    toggleEmailExtraContent()
                }
            }
        }

        if (config.type == CreateItemType.Calendar) {
            fieldViews["title"]?.setOnClickListener {
                showCalendarLocation()
                editTexts["title"]?.requestFocus()
            }
            editTexts["title"]?.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    showCalendarLocation()
                }
            }
            switches["allDay"]?.setOnCheckedChangeListener { _, checked ->
                textValues["startTime"]?.text = if (checked) "Jan 8" else "Jan 8 9:30"
                textValues["endTime"]?.text = if (checked) "Jan 8" else "11:30"
            }
        }
    }

    private fun createFieldView(field: CreateFieldConfig): View {
        val wrapper = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, 0, 0, dp(14))
        }

        field.label?.let { label ->
            wrapper.addView(createLabel(label))
        }

        when (field.type) {
            CreateFieldType.INPUT -> wrapper.addView(createInputRow(field, multiline = false, showCounter = false))
            CreateFieldType.MULTILINE -> wrapper.addView(createInputRow(field, multiline = true, showCounter = false))
            CreateFieldType.TEXT_COUNTER -> wrapper.addView(createInputRow(field, multiline = true, showCounter = true))
            CreateFieldType.DROPDOWN -> {
                wrapper.addView(createDropdownRow(field))
                val dropdown = createDropdownOptions(field)
                dropdown.visibility = View.GONE
                dropdownViews[field.key] = dropdown
                wrapper.addView(dropdown)
            }
            CreateFieldType.SWITCH -> wrapper.addView(createSwitchRow(field))
            CreateFieldType.TIME -> wrapper.addView(createTimeRow(field))
        }

        return wrapper
    }

    private fun createLabel(label: String): TextView {
        return TextView(this).apply {
            text = label
            setTextColor(color(R.color.gray_text))
            textSize = 13f
            setPadding(4, 0, 0, dp(6))
        }
    }

    private fun createInputRow(
        field: CreateFieldConfig,
        multiline: Boolean,
        showCounter: Boolean
    ): LinearLayout {
        val row = LinearLayout(this).apply {
            gravity = if (multiline) Gravity.TOP else Gravity.CENTER_VERTICAL
            orientation = LinearLayout.HORIZONTAL
            background = ContextCompat.getDrawable(this@CommonCreateActivity, R.drawable.bg_input)
            setPadding(dp(18), 0, dp(14), 0)
            minimumHeight = if (multiline) dp(112) else dp(48)
        }

        if (field.iconRes != 0) {
            row.addView(createIcon(field.iconRes))
        }

        val editText = EditText(this).apply {
            hint = field.hint
            setTextColor(color(R.color.black_text))
            setHintTextColor(color(R.color.gray_text))
            textSize = 14f
            background = null
            inputType = if (multiline) {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
            } else {
                InputType.TYPE_CLASS_TEXT
            }
            isSingleLine = !multiline
            gravity = if (multiline) Gravity.TOP else Gravity.CENTER_VERTICAL
            setPadding(if (field.iconRes == 0) 0 else dp(12), if (multiline) dp(12) else 0, 0, 0)
        }

        editTexts[field.key] = editText
        row.addView(editText, LinearLayout.LayoutParams(0, if (multiline) dp(112) else ViewGroup.LayoutParams.MATCH_PARENT, 1f))

        if (showCounter) {
            val counter = TextView(this).apply {
                text = "0"
                setTextColor(color(R.color.gray_text))
                textSize = 12f
                gravity = Gravity.BOTTOM
                setPadding(dp(8), 0, 0, dp(10))
            }
            textValues["${field.key}Count"] = counter
            row.addView(counter, LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT))
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    counter.text = (s?.length ?: 0).toString()
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })
        }

        row.setOnClickListener {
            editText.requestFocus()
        }

        return row
    }

    private fun createDropdownRow(field: CreateFieldConfig): LinearLayout {
        val row = LinearLayout(this).apply {
            gravity = Gravity.CENTER_VERTICAL
            orientation = LinearLayout.HORIZONTAL
            background = ContextCompat.getDrawable(this@CommonCreateActivity, R.drawable.bg_input)
            setPadding(dp(18), 0, dp(14), 0)
        }

        if (field.iconRes != 0) {
            row.addView(createIcon(field.iconRes))
        }

        val value = TextView(this).apply {
            text = field.defaultValue.ifBlank { field.options.firstOrNull().orEmpty() }
            setTextColor(color(R.color.black_text))
            textSize = 14f
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(12), 0, 0, 0)
        }
        textValues[field.key] = value
        row.addView(value, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f))

        row.addView(createIcon(R.mipmap.ic_down))
        row.setOnClickListener {
            securityDropdownVisible = !securityDropdownVisible
            dropdownViews[field.key]?.visibility =
                if (securityDropdownVisible) View.VISIBLE else View.GONE
        }

        return row
    }

    private fun createDropdownOptions(field: CreateFieldConfig): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(38), dp(8), dp(14), 0)
            field.options.forEach { option ->
                addView(TextView(this@CommonCreateActivity).apply {
                    text = option
                    setTextColor(color(R.color.black_text))
                    textSize = 14f
                    gravity = Gravity.CENTER_VERTICAL
                    setPadding(0, 0, 0, dp(10))
                    setOnClickListener {
                        textValues[field.key]?.text = option
                        dropdownViews[field.key]?.visibility = View.GONE
                        securityDropdownVisible = false
                        updatePasswordVisibility()
                    }
                })
            }
        }
    }

    private fun createSwitchRow(field: CreateFieldConfig): LinearLayout {
        val row = LinearLayout(this).apply {
            gravity = Gravity.CENTER_VERTICAL
            orientation = LinearLayout.HORIZONTAL
            background = ContextCompat.getDrawable(this@CommonCreateActivity, R.drawable.bg_input)
            setPadding(dp(18), 0, dp(14), 0)
        }

        row.addView(TextView(this).apply {
            text = field.label.orEmpty()
            setTextColor(color(R.color.black_text))
            textSize = 14f
            gravity = Gravity.CENTER_VERTICAL
        }, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f))

        val switchView = SwitchCompat(this).apply {
            isChecked = field.defaultValue == "true"
        }
        switches[field.key] = switchView
        row.addView(switchView)

        return row
    }

    private fun createTimeRow(field: CreateFieldConfig): LinearLayout {
        val row = LinearLayout(this).apply {
            gravity = Gravity.CENTER_VERTICAL
            orientation = LinearLayout.HORIZONTAL
            background = ContextCompat.getDrawable(this@CommonCreateActivity, R.drawable.bg_input)
            setPadding(dp(18), 0, dp(14), 0)
            setOnClickListener {
                Toast.makeText(this@CommonCreateActivity, "Select ${field.label.orEmpty().lowercase()}", Toast.LENGTH_SHORT).show()
            }
        }

        row.addView(TextView(this).apply {
            text = field.label.orEmpty()
            setTextColor(color(R.color.black_text))
            textSize = 14f
            gravity = Gravity.CENTER_VERTICAL
        }, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f))

        val value = TextView(this).apply {
            text = field.defaultValue
            setTextColor(color(R.color.gray_text))
            textSize = 14f
            gravity = Gravity.CENTER_VERTICAL
        }
        textValues[field.key] = value
        row.addView(value)

        return row
    }

    private fun createQuickTextRow(targetEditText: EditText?): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, dp(6), 0, dp(14))

            addView(createQuickTextView("www.").apply {
                setOnClickListener {
                    targetEditText?.let { editText ->
                        addPrefix(editText, "www.")
                    }
                }
            })

            addView(createQuickTextView(".com").apply {
                val params = LinearLayout.LayoutParams(dp(96), dp(34))
                params.marginStart = dp(10)
                layoutParams = params
                setOnClickListener {
                    targetEditText?.let { editText ->
                        addSuffix(editText, ".com")
                    }
                }
            })
        }
    }

    private fun createQuickTextView(textValue: String): TextView {
        return TextView(this).apply {
            text = textValue
            gravity = Gravity.CENTER
            background = ContextCompat.getDrawable(this@CommonCreateActivity, R.drawable.bg_input)
            setTextColor(color(R.color.black_text))
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(dp(96), dp(34))
        }
    }

    private fun createIcon(iconRes: Int): AppCompatImageView {
        return AppCompatImageView(this).apply {
            setImageResource(iconRes)
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            setPadding(0, dp(14), 0, dp(14))
            layoutParams = LinearLayout.LayoutParams(dp(22), ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    private fun toggleEmailExtraContent() {
        emailExpanded = !emailExpanded
        fieldViews["subject"]?.visibility = if (emailExpanded) View.VISIBLE else View.GONE
        fieldViews["content"]?.visibility = if (emailExpanded) View.VISIBLE else View.GONE
    }

    private fun showCalendarLocation() {
        fieldViews["location"]?.visibility = View.VISIBLE
    }

    private fun updatePasswordVisibility() {
        fieldViews["password"]?.visibility =
            if (textValues["security"]?.text.toString() == "None") View.GONE else View.VISIBLE
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

    private fun handleCreate() {
        val values = collectValues()
        val switchValues = switches.mapValues { entry -> entry.value.isChecked }

        val missingField = config.fields.firstOrNull { field ->
            field.required && values[field.key].orEmpty().isBlank()
        }
        if (config.mode == CreatePageMode.APP && values["appInput"].orEmpty().isBlank()) {
            Toast.makeText(this, "Please enter ${config.title}", Toast.LENGTH_SHORT).show()
            return
        }
        if (missingField != null) {
            Toast.makeText(this, "Please enter ${missingField.hint.ifBlank { missingField.label.orEmpty() }}", Toast.LENGTH_SHORT).show()
            return
        }

        val content = CreatePageConfigs.createContent(
            type = config.type,
            selectedTab = selectedTab,
            values = values,
            switches = switchValues
        )
        //TODO 创建二维码
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show()
    }

    private fun collectValues(): Map<String, String> {
        val values = mutableMapOf<String, String>()
        values["appInput"] = binding.etAppInput.text.toString().trim()

        editTexts.forEach { entry ->
            values[entry.key] = entry.value.text.toString().trim()
        }

        textValues.forEach { entry ->
            values[entry.key] = entry.value.text.toString().trim()
        }

        return values
    }

    private fun color(colorRes: Int): Int {
        return ContextCompat.getColor(this, colorRes)
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}
