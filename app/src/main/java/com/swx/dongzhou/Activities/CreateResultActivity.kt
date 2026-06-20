package com.swx.dongzhou.Activities

import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.swx.dongzhou.BaseActivity
import com.swx.dongzhou.HistoryDatabase.HistoryDatabase
import com.swx.dongzhou.R
import com.swx.dongzhou.Util.QRCodeGenerator
import com.swx.dongzhou.Util.QRCodeType
import com.swx.dongzhou.Util.Utils
import com.swx.dongzhou.databinding.ActivityCreateResultBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateResultActivity : BaseActivity<ActivityCreateResultBinding>(
    ActivityCreateResultBinding::inflate
) {
    private lateinit var type: QRCodeType
    private var historyId = -1L
    private var isFavorite = false

    override fun initView() {
        val typeName = intent.getStringExtra("type").orEmpty()
        type = runCatching {
            QRCodeType.valueOf(typeName)
        }.getOrDefault(QRCodeType.Text)
        historyId = intent.getLongExtra("historyId", -1L)

        binding.titleIcon.setImageResource(Utils.getItemImage(type))
        binding.tvTitle.text = "Barcode"
        binding.tvContent.text = intent.getStringExtra("content").orEmpty()
        showQRCode()
        loadFavoriteState()

    }

    override fun initAction() {
        binding.btnBack.setOnClickListener {
            finish()
        }
        binding.btnFavorite.setOnClickListener {
            toggleFavorite()
        }
        //TODO share,save
    }

    private fun showQRCode(){
        val content = intent.getStringExtra("content")?:""
        lifecycleScope.launch {
            val bitmap = QRCodeGenerator.generateQRCode(content)
            binding.qrCodeHolder.setImageBitmap(bitmap)
        }
    }

    private fun loadFavoriteState() {
        if (historyId <= 0) {
            updateFavoriteIcon()
            return
        }
        lifecycleScope.launch {
            val history = withContext(Dispatchers.IO) {
                HistoryDatabase.getDatabase(this@CreateResultActivity).HistoryDao().selectById(historyId)
            }
            isFavorite = history?.isFavorite == true
            updateFavoriteIcon()
        }
    }

    private fun toggleFavorite() {
        if (historyId <= 0) {
            Toast.makeText(this, "History not found", Toast.LENGTH_SHORT).show()
            return
        }
        lifecycleScope.launch {
            try {
                val nextFavorite = !isFavorite
                withContext(Dispatchers.IO) {
                    HistoryDatabase.getDatabase(this@CreateResultActivity).HistoryDao().updateFavorite(
                        id = historyId,
                        isFavorite = nextFavorite,
                        favoriteAt = if (nextFavorite) System.currentTimeMillis() else null
                    )
                }
                isFavorite = nextFavorite
                updateFavoriteIcon()
            } catch (e: Exception) {
                Toast.makeText(this@CreateResultActivity, "Update favorite failed", Toast.LENGTH_SHORT).show()
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
}
