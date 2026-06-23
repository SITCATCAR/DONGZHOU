package com.swx.dongzhou.pages.settingPage

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.swx.dongzhou.BaseFragment
import com.swx.dongzhou.MainActivity
import com.swx.dongzhou.R
import com.swx.dongzhou.databinding.SettingFragmentBinding

class SettingFragment : BaseFragment<SettingFragmentBinding>(
    SettingFragmentBinding::inflate
) {

    private var isDarkMode = false

    override fun initView() {
        isDarkMode = getSavedDarkMode()
        updateDarkModeSwitch()
        binding.layoutDarkMode.setOnClickListener {
            setDarkMode(!isDarkMode)
        }
        binding.layoutDarkModeSwitch.setOnClickListener {
            setDarkMode(!isDarkMode)
        }
    }

    override fun loadData() {
    }

    private fun setDarkMode(enabled: Boolean) {
        isDarkMode = enabled
        requireContext()
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_DARK_MODE, enabled)
            .apply()
        updateDarkModeSwitch()
        (requireActivity() as? MainActivity)?.openSettingAfterRecreate()
        AppCompatDelegate.setDefaultNightMode(
            if (enabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    private fun updateDarkModeSwitch() {
        binding.viewDarkModeTrack.setBackgroundResource(
            if (isDarkMode) {
                R.drawable.bg_setting_switch_track_on
            } else {
                R.drawable.bg_setting_switch_track_off
            }
        )
        binding.viewDarkModeThumb.translationX = if (isDarkMode) dp(SWITCH_THUMB_OFFSET) else 0f
    }

    private fun getSavedDarkMode(): Boolean {
        return requireContext()
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_DARK_MODE, false)
    }

    private fun dp(value: Int): Float {
        return value * resources.displayMetrics.density
    }

    companion object {
        const val PREFS_NAME = "setting_prefs"
        const val KEY_DARK_MODE = "key_dark_mode"
        private const val SWITCH_THUMB_OFFSET = 21
    }
}
