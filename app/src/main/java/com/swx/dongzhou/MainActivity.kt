package com.swx.dongzhou

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.swx.dongzhou.Util.ThemeModeManager
import com.swx.dongzhou.databinding.ActivityMainBinding
import com.swx.dongzhou.pages.createPage.CreateFragment
import com.swx.dongzhou.pages.historyPage.HistoryFragment
import com.swx.dongzhou.pages.scanPage.ScanFragment
import com.swx.dongzhou.pages.settingPage.SettingFragment

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    //懒加载fragment，避免切换dark模式后退出重新打开rcv不应用dark颜色
    private val scanFragment by lazy {
        supportFragmentManager.findFragmentByTag(TAG_SCAN) as? ScanFragment ?: ScanFragment()
    }
    private val createFragment by lazy {
        supportFragmentManager.findFragmentByTag(TAG_CREATE) as? CreateFragment ?: CreateFragment()
    }
    private val historyFragment by lazy {
        supportFragmentManager.findFragmentByTag(TAG_HISTORY) as? HistoryFragment ?: HistoryFragment()
    }
    private val settingFragment by lazy {
        supportFragmentManager.findFragmentByTag(TAG_SETTING) as? SettingFragment ?: SettingFragment()
    }
    private var currentTag = TAG_SCAN

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeModeManager.applySavedNightMode(this)
        currentTag = savedInstanceState?.getString(KEY_CURRENT_TAG) ?: TAG_SCAN
        super.onCreate(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(KEY_CURRENT_TAG, currentTag)
        super.onSaveInstanceState(outState)
    }

    fun openSettingAfterRecreate() {
        intent.putExtra(EXTRA_OPEN_SETTING, true)
    }

    override fun initData() {
    }

    override fun initView() {
        enableInsetsView(binding.bottomBar.root, left = false, top = false, right = false, bottom = true)
        val shouldOpenSetting = intent.getBooleanExtra(EXTRA_OPEN_SETTING, false)
        intent.putExtra(EXTRA_OPEN_SETTING, false)

        showFragment(if (shouldOpenSetting) TAG_SETTING else currentTag)
    }

    override fun initAction() {
        super.initAction()
        binding.bottomBar.bottomScan.setOnClickListener { showFragment(TAG_SCAN) }
        binding.bottomBar.bottomHistory.setOnClickListener { showFragment(TAG_HISTORY) }
        binding.bottomBar.bottomCreate.setOnClickListener { showFragment(TAG_CREATE) }
        binding.bottomBar.bottomSetting.setOnClickListener { showFragment(TAG_SETTING) }
    }

    private fun showFragment(tag: String) {
        val targetFragment = getOrCreateFragment(tag)
        setBottomBarIcon(tag)
        val tx = supportFragmentManager.beginTransaction()
        supportFragmentManager.fragments.forEach { fragment ->
            if (fragment != targetFragment && fragment.isAdded) {
                tx.hide(fragment)
            }
        }
        if (targetFragment.isAdded) {
            tx.show(targetFragment)
        } else {
            tx.add(R.id.fragmentHolder, targetFragment, tag)
        }
        tx.commit()
        currentTag = tag
    }

    private fun getOrCreateFragment(tag: String): Fragment {
        return when (tag) {
            TAG_SCAN -> scanFragment
            TAG_HISTORY -> historyFragment
            TAG_CREATE -> createFragment
            TAG_SETTING -> settingFragment
            else -> scanFragment
        }
    }

    private fun setBottomBarIcon(tag: String) {
        val selectedColor = ContextCompat.getColor(this, R.color.blue_text)
        val normalColor = ContextCompat.getColor(this, R.color.gray_text)

        binding.bottomBar.iconScan.setImageResource(R.mipmap.ic_scan)
        binding.bottomBar.iconHistory.setImageResource(R.mipmap.ic_history)
        binding.bottomBar.iconCreate.setImageResource(R.mipmap.ic_create)
        binding.bottomBar.iconSetting.setImageResource(R.mipmap.ic_setting)

        binding.bottomBar.textScan.setTextColor(normalColor)
        binding.bottomBar.textHistory.setTextColor(normalColor)
        binding.bottomBar.textCreate.setTextColor(normalColor)
        binding.bottomBar.textSetting.setTextColor(normalColor)

        when (tag) {
            TAG_SCAN -> {
                binding.bottomBar.iconScan.setImageResource(R.mipmap.ic_scan_selected)
                binding.bottomBar.textScan.setTextColor(selectedColor)
            }
            TAG_HISTORY -> {
                binding.bottomBar.iconHistory.setImageResource(R.mipmap.ic_history_selected)
                binding.bottomBar.textHistory.setTextColor(selectedColor)
            }
            TAG_CREATE -> {
                binding.bottomBar.iconCreate.setImageResource(R.mipmap.ic_create_selected)
                binding.bottomBar.textCreate.setTextColor(selectedColor)
            }
            TAG_SETTING -> {
                binding.bottomBar.iconSetting.setImageResource(R.mipmap.ic_setting_selected)
                binding.bottomBar.textSetting.setTextColor(selectedColor)
            }
        }
    }

    companion object {
        private const val TAG_SCAN = "scan"
        private const val TAG_HISTORY = "history"
        private const val TAG_CREATE = "create"
        private const val TAG_SETTING = "setting"
        private const val EXTRA_OPEN_SETTING = "extra_open_setting"
        private const val KEY_CURRENT_TAG = "key_current_tag"
    }
}
