package com.swx.dongzhou.pages.settingPage

import com.swx.dongzhou.App
import com.swx.dongzhou.BaseFragment
import com.swx.dongzhou.MainActivity
import com.swx.dongzhou.Util.ThemeModeManager
import com.swx.dongzhou.databinding.SettingFragmentBinding

class SettingFragment : BaseFragment<SettingFragmentBinding>(
    SettingFragmentBinding::inflate
) {

    private var isDarkMode = false

    override fun initView() {
        isDarkMode = getSavedDarkMode()
        binding.darkModeSwitch.isSaveEnabled = false
        binding.darkModeSwitch.isChecked = isDarkMode
        binding.layoutDarkMode.setOnClickListener {
            binding.darkModeSwitch.isChecked = !binding.darkModeSwitch.isChecked
        }
        binding.darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            setDarkMode(isChecked)
        }
    }

    override fun loadData() {
    }

    private fun setDarkMode(enabled: Boolean) {
        if (enabled == isDarkMode) {
            return
        }
        isDarkMode = enabled
        (requireActivity() as? MainActivity)?.openSettingAfterRecreate()
        ThemeModeManager.setDarkMode(App.context, enabled)
    }

    private fun getSavedDarkMode(): Boolean {
        return ThemeModeManager.getSavedDarkMode(App.context)
    }

    companion object {
        const val PREFS_NAME = ThemeModeManager.PREFS_NAME
        const val KEY_DARK_MODE = ThemeModeManager.KEY_DARK_MODE
    }
}
