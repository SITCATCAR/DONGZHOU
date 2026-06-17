package com.swx.dongzhou

import com.swx.dongzhou.databinding.ActivityMainBinding
import com.swx.dongzhou.pages.createPage.CreateFragment

class MainActivity :BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    private lateinit var createFragment: CreateFragment

    override fun initView() {
        createFragment= CreateFragment()

        supportFragmentManager.beginTransaction().add(R.id.fragmentHolder, createFragment)
            .commit()
    }


}