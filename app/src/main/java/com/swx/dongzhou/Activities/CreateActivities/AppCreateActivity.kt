package com.swx.dongzhou.Activities.CreateActivities

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.swx.dongzhou.Activities.CreateResultActivity
import com.swx.dongzhou.BaseActivity
import com.swx.dongzhou.HistoryDatabase.History
import com.swx.dongzhou.HistoryDatabase.HistoryDatabase
import com.swx.dongzhou.R
import com.swx.dongzhou.Util.QRCodeType
import com.swx.dongzhou.databinding.ActivityAppCreateBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppCreateActivity : BaseActivity<ActivityAppCreateBinding>(
    ActivityAppCreateBinding::inflate
) {
    private lateinit var config: CreatePageConfig
    private var selectedTab = ""

    override fun initData() {
        val typeName = intent.getStringExtra(CreatePageConfigs.EXTRA_CREATE_TYPE)
        val type = runCatching {
            QRCodeType.valueOf(typeName.orEmpty())
        }.getOrDefault(QRCodeType.Youtube)

        config = CreatePageConfigs.getConfig(type)
        selectedTab = config.tabs.firstOrNull().orEmpty()
    }

    override fun initView() {
        enableInsetsView(binding.root, top = true, bottom = true)
        binding.tvTitle.text = config.title
        binding.ivAppIcon.setImageResource(config.iconRes)
        binding.tvOpenTag.visibility = if (config.showOpenTag) View.VISIBLE else View.GONE
        binding.tvCountryCode.text = config.countryCode
        binding.tvCountryCode.visibility = if (config.showCountryCode) View.VISIBLE else View.GONE
        binding.viewCountryDivider.visibility = View.GONE
        binding.tvAppDescription.text = config.description
        binding.tvAppDescription.visibility = if (config.description.isNotBlank()) View.VISIBLE else View.GONE
        binding.layoutQuickText.visibility = if (config.showQuickText) View.VISIBLE else View.GONE
        binding.layoutAppInput.setPadding(
            if (config.showCountryCode) 0 else dp(18),
            0,
            dp(14),
            0
        )
        binding.tvCountryCode.setBackgroundResource(R.drawable.bg_country_code)

        initTabs()
        updateAppHint()
    }

    override fun initAction() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.tvPrefixWww.setOnClickListener {
            addPrefix(binding.etAppInput, "www.")
        }

        binding.tvSuffixCom.setOnClickListener {
            addSuffix(binding.etAppInput, ".com")
        }

        binding.etAppInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateCreateButtonState()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.btnCreate.setOnClickListener {
            if (!isCreateEnabled()) {
                return@setOnClickListener
            }
            handleCreate()
        }

        updateCreateButtonState()
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

    private fun updateCreateButtonState() {
        val canCreate = isCreateEnabled()
        binding.btnCreate.isEnabled = canCreate
        binding.btnCreate.setBackgroundResource(
            if (canCreate) R.drawable.bg_button_enable else R.drawable.bg_button_unable
        )
    }

    private fun isCreateEnabled(): Boolean {
        return binding.etAppInput.text.toString().trim().isNotEmpty()
    }

    private fun handleCreate() {
        val input = binding.etAppInput.text.toString().trim()
        if (input.isEmpty()) {
            Toast.makeText(this, "Please enter ${config.title}", Toast.LENGTH_SHORT).show()
            return
        }

        val content = CreatePageConfigs.createContent(
            type = config.type,
            selectedTab = selectedTab,
            values = mapOf("appInput" to input),
            switches = emptyMap()
        )

        saveHistoryAndOpenResult(input, content)
    }

    private fun saveHistoryAndOpenResult(title: String, content: String) {
        lifecycleScope.launch {
            try {
                val historyId = withContext(Dispatchers.IO) {
                    val history = History(
                        title = title.ifBlank { content },
                        content = content,
                        type = config.type
                    )
                    HistoryDatabase.getDatabase(this@AppCreateActivity).HistoryDao().insert(history)
                }
                val intent = Intent(this@AppCreateActivity, CreateResultActivity::class.java).apply {
                    putExtra("type", config.type.name)
                    putExtra("content", content)
                    putExtra("historyId", historyId)
                }
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this@AppCreateActivity, "Save history failed", Toast.LENGTH_SHORT).show()
            }
        }
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

    private fun color(colorRes: Int): Int {
        return ContextCompat.getColor(this, colorRes)
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}
