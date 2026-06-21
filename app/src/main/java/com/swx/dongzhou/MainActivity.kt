package com.swx.dongzhou

import android.util.Log
import com.swx.dongzhou.Util.QRCodeGenerator
import com.swx.dongzhou.databinding.ActivityMainBinding
import com.swx.dongzhou.pages.createPage.CreateFragment
import com.swx.dongzhou.pages.historyPage.HistoryFragment
import com.swx.dongzhou.pages.scanPage.ScanFragment

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    private lateinit var scanFragment: ScanFragment
    private lateinit var createFragment: CreateFragment
    private lateinit var historyFragment: HistoryFragment

    override fun initView() {
        scanFragment = ScanFragment()
        createFragment = CreateFragment()
        historyFragment = HistoryFragment()

        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentHolder, scanFragment, TAG_SCAN)
            .add(R.id.fragmentHolder, createFragment, TAG_CREATE)
            .add(R.id.fragmentHolder, historyFragment, TAG_HISTORY)
            .hide(createFragment)
            .hide(historyFragment)
            .commit()
    }

    override fun initAction() {
        super.initAction()
        binding.bottomBar.bottomScan.setOnClickListener { showFragment(TAG_SCAN) }
        binding.bottomBar.bottomHistory.setOnClickListener { showFragment(TAG_HISTORY) }
        binding.bottomBar.bottomCreate.setOnClickListener { showFragment(TAG_CREATE) }
    }

    private fun showFragment(tag: String) {
        val tx = supportFragmentManager.beginTransaction()
        listOf(scanFragment, createFragment, historyFragment).forEach { frag ->
            if (frag.tag == tag) tx.show(frag) else tx.hide(frag)
        }
        tx.commit()
    }

    companion object {
        private const val TAG_SCAN = "scan"
        private const val TAG_HISTORY = "history"
        private const val TAG_CREATE = "create"
    }
}
