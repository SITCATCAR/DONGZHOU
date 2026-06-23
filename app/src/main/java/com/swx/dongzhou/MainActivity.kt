package com.swx.dongzhou

import androidx.core.content.ContextCompat
import com.swx.dongzhou.databinding.ActivityMainBinding
import com.swx.dongzhou.pages.createPage.CreateFragment
import com.swx.dongzhou.pages.historyPage.HistoryFragment
import com.swx.dongzhou.pages.scanPage.ScanFragment
import com.swx.dongzhou.pages.settingPage.SettingFragment

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    private lateinit var scanFragment: ScanFragment
    private lateinit var createFragment: CreateFragment
    private lateinit var historyFragment: HistoryFragment
    private lateinit var settingFragment: SettingFragment

    override fun initView() {
        scanFragment = ScanFragment()
        createFragment = CreateFragment()
        historyFragment = HistoryFragment()
        settingFragment = SettingFragment()

        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentHolder, scanFragment, TAG_SCAN)
            .add(R.id.fragmentHolder, createFragment, TAG_CREATE)
            .add(R.id.fragmentHolder, historyFragment, TAG_HISTORY)
            .add(R.id.fragmentHolder, settingFragment, TAG_SETTING)
            .hide(createFragment)
            .hide(historyFragment)
            .hide(settingFragment)
            .commit()
        setBottomBarIcon(TAG_SCAN)
    }

    override fun initAction() {
        super.initAction()
        binding.bottomBar.bottomScan.setOnClickListener { showFragment(TAG_SCAN) }
        binding.bottomBar.bottomHistory.setOnClickListener { showFragment(TAG_HISTORY) }
        binding.bottomBar.bottomCreate.setOnClickListener { showFragment(TAG_CREATE) }
        binding.bottomBar.bottomSetting.setOnClickListener { showFragment(TAG_SETTING) }
    }

    private fun showFragment(tag: String) {
        setBottomBarIcon(tag)
        val tx = supportFragmentManager.beginTransaction()
        listOf(scanFragment, createFragment, historyFragment, settingFragment).forEach { frag ->
            if (frag.tag == tag) {
                tx.show(frag)
            } else {
                tx.hide(frag)
            }
        }
        tx.commit()
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
    }
}
