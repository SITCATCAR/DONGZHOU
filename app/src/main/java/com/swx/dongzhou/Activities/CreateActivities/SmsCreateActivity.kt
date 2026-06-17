package com.swx.dongzhou.Activities.CreateActivities

import com.swx.dongzhou.BaseActivity
import com.swx.dongzhou.databinding.ActivitySmsCreateBinding

class SmsCreateActivity : BaseActivity<ActivitySmsCreateBinding>(
    ActivitySmsCreateBinding::inflate
) {
    override fun initView() {
        enableInsetsView(binding.root, top = true, bottom = true)
    }

    override fun initAction() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnCreate.setOnClickListener {
            val phone = binding.etPhoneNumber.text.toString().trim()
            val message = binding.etMessageContent.text.toString().trim()

            // TODO: 生成 SMS 二维码内容
            // 格式示例：SMSTO:$phone:$message
        }
    }
}