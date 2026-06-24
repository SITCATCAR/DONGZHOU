package com.swx.dongzhou.Activities

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.app.Activity
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.swx.dongzhou.Activities.CreateActivities.CreatePageConfigs
import com.swx.dongzhou.BaseActivity
import com.swx.dongzhou.HistoryDatabase.History
import com.swx.dongzhou.HistoryDatabase.HistoryDatabase
import com.swx.dongzhou.R
import com.swx.dongzhou.Util.QRCodeType
import com.swx.dongzhou.Util.Utils
import com.swx.dongzhou.databinding.ActivityScanResultBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScanResultActivity : BaseActivity<ActivityScanResultBinding>(
    ActivityScanResultBinding::inflate
) {
    private var content = ""
    private var type = QRCodeType.Text
    private var historyId = -1L
    private var isFavorite = false

    override fun initData() {
        content = intent.getStringExtra(EXTRA_SCAN_RESULT).orEmpty()
        type = runCatching {
            QRCodeType.valueOf(intent.getStringExtra(EXTRA_SCAN_TYPE).orEmpty())
        }.getOrDefault(QRCodeType.Text)
        historyId = intent.getLongExtra(EXTRA_HISTORY_ID, -1L)
    }

    override fun initView() {
        initDarkModel()
        binding.tvTitle.text = CreatePageConfigs.getConfig(type).title
        binding.titleIcon.setImageResource(Utils.getItemImage(type))
        binding.tvContent.text = content
        loadFavoriteState()
    }

    //返回箭头是图片，dark模式只能更换图片。
    private fun initDarkModel(){
        if(AppCompatDelegate.getDefaultNightMode()== AppCompatDelegate.MODE_NIGHT_YES){
            binding.btnBack.setImageResource(R.mipmap.ic_results_page_return_white)
        }
    }
    override fun initAction() {
        binding.btnBack.setOnClickListener {
            finish()
        }
        binding.btnFavorite.setOnClickListener {
            toggleFavorite()
        }
        binding.btnCopy.setOnClickListener {
            copyContent()
        }
        binding.btnShare.setOnClickListener {
            shareContent()
        }
    }

    private fun loadFavoriteState() {
        if (historyId <= 0) {
            updateFavoriteIcon()
            return
        }
        lifecycleScope.launch {
            val history = withContext(Dispatchers.IO) {
                HistoryDatabase.getDatabase(this@ScanResultActivity).HistoryDao().selectById(historyId)
            }
            isFavorite = history?.isFavorite == true
            updateFavoriteIcon()
        }
    }

    private fun toggleFavorite() {
        lifecycleScope.launch {
            try {
                val nextFavorite = !isFavorite
                val nextFavoriteAt = if (nextFavorite) System.currentTimeMillis() else null
                val nextHistoryId = withContext(Dispatchers.IO) {
                    val dao = HistoryDatabase.getDatabase(this@ScanResultActivity).HistoryDao()
                    if (historyId > 0) {
                        dao.updateFavorite(
                            id = historyId,
                            isFavorite = nextFavorite,
                            favoriteAt = nextFavoriteAt
                        )
                        historyId
                    } else {
                        dao.insert(
                            History(
                                title = content.take(30).ifBlank { "Scan Result" },
                                content = content,
                                type = type,
                                isFavorite = true,
                                favoriteAt = System.currentTimeMillis()
                            )
                        )
                    }
                }
                historyId = nextHistoryId
                isFavorite = nextFavorite
                updateFavoriteIcon()
                setResult(Activity.RESULT_OK)
            } catch (e: Exception) {
                Toast.makeText(this@ScanResultActivity, "Update favorite failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateFavoriteIcon() {
        binding.btnFavorite.setImageResource(
            if (isFavorite) {
                R.mipmap.ic_favorites_selected
            } else {
                R.mipmap.ic_results_favorites
            }
        )
    }

    private fun copyContent() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("Scan result", content))
        Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show()
    }

    private fun shareContent() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, content)
        }
        startActivity(Intent.createChooser(intent, "Share"))
    }

    companion object {
        const val EXTRA_SCAN_RESULT = "scan_result"
        const val EXTRA_SCAN_TYPE = "scan_type"
        const val EXTRA_HISTORY_ID = "history_id"
    }
}
