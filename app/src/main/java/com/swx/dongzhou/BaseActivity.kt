package com.swx.dongzhou


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.viewbinding.ViewBinding

abstract class BaseActivity <vb: ViewBinding>(val inflate:(LayoutInflater)->vb) : AppCompatActivity() {

    protected lateinit var binding: vb


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        initView()
        initAction()
    }

    protected abstract fun initView()

    protected open fun initData(){}
    protected open fun initAction(){}


    protected fun enableInsetsView(view: View, top: Boolean,bottom: Boolean){
        //同BaseFragment,防止丢失边距
        val initialLeft = view.paddingLeft
        val initialTop = view.paddingTop
        val initialRight = view.paddingRight
        val initialBottom = view.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets->
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())

            val chosenBottom = if (imeVisible) ime.bottom else systemBars.bottom

            v.updatePadding(
                left = initialLeft + systemBars.left,
                top = if (top) initialTop + systemBars.top else initialTop,
                right = initialRight + systemBars.right,
                bottom = if (bottom) initialBottom + chosenBottom else initialBottom
            )

            insets
        }
    }

}
