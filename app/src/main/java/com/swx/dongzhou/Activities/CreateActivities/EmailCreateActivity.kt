package com.swx.dongzhou.Activities.CreateActivities

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.swx.dongzhou.BaseActivity
import com.swx.dongzhou.R
import com.swx.dongzhou.databinding.ActivityEmailCreateBinding

class EmailCreateActivity : BaseActivity<ActivityEmailCreateBinding>(
    ActivityEmailCreateBinding::inflate) {

    private var emailExpanded = false

    override fun initView() {
    }

    override fun initAction() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.layoutEmailInput.setOnClickListener {
            toggleEmailExtraContent()
        }

        binding.ivExpandEmail.setOnClickListener {
            toggleEmailExtraContent()
        }
    }


    private fun toggleEmailExtraContent() {
        emailExpanded = !emailExpanded

        binding.layoutExtraEmailContent.visibility =
            if (emailExpanded) View.VISIBLE else View.GONE
    }

}