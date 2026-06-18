package com.swx.dongzhou.Activities.CreateActivities

import android.content.Intent
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.swx.dongzhou.Activities.CreateResultActivity
import com.swx.dongzhou.BaseActivity
import com.swx.dongzhou.R
import com.swx.dongzhou.databinding.ActivityAppCreateBinding
import com.swx.dongzhou.pages.createPage.CreateItemType

class AppCreateActivity : BaseActivity<ActivityAppCreateBinding>(
    ActivityAppCreateBinding::inflate
) {
    private lateinit var config: CreatePageConfig
    private var selectedTab = ""

    override fun initData() {
        val typeName = intent.getStringExtra(CreatePageConfigs.EXTRA_CREATE_TYPE)
        val type = runCatching {
            CreateItemType.valueOf(typeName.orEmpty())
        }.getOrDefault(CreateItemType.Youtube)

        config = CreatePageConfigs.getConfig(type)
        selectedTab = config.tabs.firstOrNull().orEmpty()
    }

    override fun initView() {
        enableInsetsView(binding.root, top = true, bottom = true)
        binding.tvTitle.text = config.title
        binding.ivAppIcon.setImageResource(config.iconRes)
        binding.tvOpenTag.visibility = if (config.showOpenTag) View.VISIBLE else View.GONE
        binding.tvCountryCode.visibility = if (config.showCountryCode) View.VISIBLE else View.GONE
        binding.viewCountryDivider.visibility = if (config.showCountryCode) View.VISIBLE else View.GONE
        binding.layoutQuickText.visibility = if (config.showQuickText) View.VISIBLE else View.GONE

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

        binding.btnCreate.setOnClickListener {
            handleCreate()
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
        //TODO 创建二维码
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show()
        val intent = Intent(this, CreateResultActivity::class.java).apply {
            putExtra("type",config.type.name)
            putExtra("content",content)
        }
        startActivity(intent)
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
}
