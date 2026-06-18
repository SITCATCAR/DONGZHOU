package com.swx.dongzhou.Activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.swx.dongzhou.BaseActivity
import com.swx.dongzhou.R
import com.swx.dongzhou.Util.QRCodeGenerator
import com.swx.dongzhou.Util.Utils
import com.swx.dongzhou.databinding.ActivityCreateResultBinding
import com.swx.dongzhou.pages.createPage.CreateItemType
import kotlinx.coroutines.launch

class CreateResultActivity : BaseActivity<ActivityCreateResultBinding>(
    ActivityCreateResultBinding::inflate
) {
    private lateinit var type: String
    override fun initView() {
        type = intent.getStringExtra("type")?:""

        binding.titleIcon.setImageResource(Utils.getItemImage(enumValueOf(type)))
        showQRCode()

    }

    override fun initAction() {
        binding.btnBack.setOnClickListener {
            finish()
        }
        //TODO share,save
    }

     fun showQRCode(){
        val content = intent.getStringExtra("content")?:""
        lifecycleScope.launch {
            val bitmap = QRCodeGenerator.generateQRCode(content)
            binding.qrCodeHolder.setImageBitmap(bitmap)
        }
    }
}