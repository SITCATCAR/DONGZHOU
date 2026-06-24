package com.swx.dongzhou

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.swx.dongzhou.Util.ThemeModeManager

class App : Application() {

    companion object {
        const val SP_NAME = "dongzhouPrefs"

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        ThemeModeManager.applySavedNightMode(this)
    }
}
