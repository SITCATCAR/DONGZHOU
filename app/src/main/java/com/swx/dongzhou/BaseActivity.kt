package com.swx.dongzhou

import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updatePadding
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<VB : ViewBinding>(val inflate: (LayoutInflater) -> VB) : AppCompatActivity() {

    protected lateinit var binding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflate(layoutInflater)
        setContentView(binding.root)
        makeAppEdgeToEdge()
        initData()
        initView()
        initAction()
    }

    protected abstract fun initView()

    protected open fun initData() {}
    protected open fun initAction() {}

    private fun makeAppEdgeToEdge() {
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.setNavigationBarContrastEnforced(false)
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            window.navigationBarColor = Color.BLACK
        }

        val isLightMode = !isNightMode()
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = isLightMode
            isAppearanceLightNavigationBars = isLightMode
        }
    }

    private fun isNightMode(): Boolean {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            return true
        }
        return (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
            Configuration.UI_MODE_NIGHT_YES
    }

    protected fun enableInsetsView(
        view: View,
        left: Boolean,
        top: Boolean,
        right: Boolean,
        bottom: Boolean
    ) {
        val initialLeft = view.paddingLeft
        val initialTop = view.paddingTop
        val initialRight = view.paddingRight
        val initialBottom = view.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            val chosenBottom = maxOf(ime.bottom, systemBars.bottom)

            v.updatePadding(
                left = if (left) initialLeft + systemBars.left else initialLeft,
                top = if (top) initialTop + systemBars.top else initialTop,
                right = if (right) initialRight + systemBars.right else initialRight,
                bottom = if (bottom) initialBottom + chosenBottom else initialBottom
            )

            insets
        }
    }
}
