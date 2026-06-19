package com.swx.dongzhou

import android.util.Log
import com.swx.dongzhou.Util.QRCodeGenerator
import com.swx.dongzhou.databinding.ActivityMainBinding
import com.swx.dongzhou.pages.createPage.CreateFragment
import com.swx.dongzhou.pages.historyPage.HistoryFragment

class MainActivity :BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    private lateinit var createFragment: CreateFragment
    private lateinit var historyFragment: HistoryFragment

    override fun initView() {
        createFragment= CreateFragment()
        historyFragment= HistoryFragment()

        supportFragmentManager.beginTransaction().add(R.id.fragmentHolder, createFragment)
            .add(R.id.fragmentHolder,historyFragment).hide(historyFragment)
            .commit()
    }

    override fun initAction() {
        super.initAction()
        binding.bottomBar.bottomHistory.setOnClickListener {
            supportFragmentManager.beginTransaction().hide(createFragment).show(historyFragment).commit()
        }
        binding.bottomBar.bottomCreate.setOnClickListener {
            supportFragmentManager.beginTransaction().hide(historyFragment).show(createFragment).commit()
        }
    }


}