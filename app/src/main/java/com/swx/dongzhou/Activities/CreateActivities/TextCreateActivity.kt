package com.swx.dongzhou.Activities.CreateActivities

import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.swx.dongzhou.BaseActivity
import com.swx.dongzhou.databinding.ActivityTextCreateBinding

class TextCreateActivity : BaseActivity<ActivityTextCreateBinding>(
    ActivityTextCreateBinding::inflate
) {

    override fun initView() {
        enableInsetsView(binding.root, top = true, bottom = true)

        binding.tvTitle.text = "Text"
        binding.tvTextCount.text = "0"
    }

    override fun initAction() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.etTextContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                binding.tvTextCount.text = (s?.length ?: 0).toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.btnCreate.setOnClickListener {
            val text = binding.etTextContent.text.toString().trim()

            if (text.isEmpty()) {
                Toast.makeText(this, "Please enter something", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            createTextQr(text)
        }
    }

    private fun createTextQr(text: String) {
        // TODO: 在这里接入二维码生成逻辑
        // 例如：
        // val intent = Intent(this, QrResultActivity::class.java)
        // intent.putExtra("content", text)
        // startActivity(intent)

        Toast.makeText(this, "Create: $text", Toast.LENGTH_SHORT).show()
    }
}