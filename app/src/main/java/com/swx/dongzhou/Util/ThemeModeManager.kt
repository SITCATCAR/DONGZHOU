package com.swx.dongzhou.Util

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

object ThemeModeManager {
    const val PREFS_NAME = "setting_prefs"
    const val KEY_DARK_MODE = "key_dark_mode"

    fun applySavedNightMode(context: Context) {
        applyNightMode(getSavedDarkMode(context))
    }

    fun setDarkMode(context: Context, enabled: Boolean) {
        context
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit {
                putBoolean(KEY_DARK_MODE, enabled)
            }
        applyNightMode(enabled)
    }

    fun getSavedDarkMode(context: Context): Boolean {
        return context
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_DARK_MODE, false)
    }

    private fun applyNightMode(enabled: Boolean) {
        val nightMode = if (enabled) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        if (AppCompatDelegate.getDefaultNightMode() != nightMode) {
            AppCompatDelegate.setDefaultNightMode(nightMode)
        }
    }
}
