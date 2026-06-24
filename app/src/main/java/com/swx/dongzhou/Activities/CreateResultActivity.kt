package com.swx.dongzhou.Activities

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.swx.dongzhou.BaseActivity
import com.swx.dongzhou.HistoryDatabase.HistoryDatabase
import com.swx.dongzhou.R
import com.swx.dongzhou.Util.ImageSaver
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
    private lateinit var bitmap: Bitmap
    private val storagePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            saveQRCode()
        } else {
            Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun initView() {
        initDarkModel()
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
        binding.btnSave.setOnClickListener {
            saveQRCode()
        }
        binding.btnShare.setOnClickListener {
            shareQRCode()
        }
    }

    private fun showQRCode() {
        val content = intent.getStringExtra("content")?:""
        lifecycleScope.launch {
            bitmap = QRCodeGenerator.generateQRCode(content)!!
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

    private fun saveQRCode() {
        if (!::bitmap.isInitialized) {
            Toast.makeText(this, "QR code is not ready", Toast.LENGTH_SHORT).show()
            return
        }
        when (val result = ImageSaver.saveToGallery(this, bitmap)) {
            ImageSaver.SaveResult.Success -> {
                Toast.makeText(this, "Saved to gallery", Toast.LENGTH_SHORT).show()
            }
            ImageSaver.SaveResult.PermissionRequired -> {
                storagePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
            is ImageSaver.SaveResult.Error -> {
                Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun shareQRCode() {
        if (!::bitmap.isInitialized) {
            Toast.makeText(this, "QR code is not ready", Toast.LENGTH_SHORT).show()
            return
        }
        val shareIntent = ImageSaver.getShareIntent(this, bitmap)
        if (shareIntent == null) {
            Toast.makeText(this, "Share failed", Toast.LENGTH_SHORT).show()
            return
        }
        startActivity(Intent.createChooser(shareIntent, "Share QR Code"))
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
