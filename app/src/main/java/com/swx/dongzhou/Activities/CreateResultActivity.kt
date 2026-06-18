package com.swx.dongzhou.Activities

import androidx.lifecycle.lifecycleScope
import com.swx.dongzhou.BaseActivity
import com.swx.dongzhou.Util.QRCodeGenerator
import com.swx.dongzhou.Util.Utils
import com.swx.dongzhou.databinding.ActivityCreateResultBinding
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