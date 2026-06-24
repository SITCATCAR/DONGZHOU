package com.swx.dongzhou.pages.settingPage

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.swx.dongzhou.App
import com.swx.dongzhou.BaseFragment
import com.swx.dongzhou.MainActivity
import com.swx.dongzhou.databinding.SettingFragmentBinding
import androidx.core.content.edit

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
        App.context
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit {
                putBoolean(KEY_DARK_MODE, enabled)
            }
        (requireActivity() as? MainActivity)?.openSettingAfterRecreate()
        val nightMode = if (enabled) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        if (AppCompatDelegate.getDefaultNightMode() != nightMode) {
            AppCompatDelegate.setDefaultNightMode(nightMode)
        }
    }

    private fun getSavedDarkMode(): Boolean {
        return App.context
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_DARK_MODE, false)
    }

    companion object {
        const val PREFS_NAME = "setting_prefs"
        const val KEY_DARK_MODE = "key_dark_mode"
    }
}
