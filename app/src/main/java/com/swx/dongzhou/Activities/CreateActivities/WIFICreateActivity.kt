package com.swx.dongzhou.Activities.CreateActivities

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.swx.dongzhou.BaseActivity
import com.swx.dongzhou.R
import com.swx.dongzhou.databinding.ActivityWificreateBinding

class WIFICreateActivity : BaseActivity<ActivityWificreateBinding>(
    ActivityWificreateBinding::inflate) {
    private var securityDropdownVisible = false
    override fun initView() {
    }

    override fun initAction() {
        binding.layoutSecurityType.setOnClickListener {
            securityDropdownVisible = !securityDropdownVisible
            binding.layoutSecurityDropdown.visibility =
                if (securityDropdownVisible) View.VISIBLE else View.GONE
        }

        binding.tvSecurityWpa.setOnClickListener {
            selectSecurityType("WPA/WPA2")
        }

        binding.tvSecurityWep.setOnClickListener {
            selectSecurityType("WEP")
        }

        binding.tvSecurityNone.setOnClickListener {
            selectSecurityType("None")
        }
    }

    private fun selectSecurityType(type: String) {
        binding.tvSecurityType.text = type
        binding.layoutSecurityDropdown.visibility = View.GONE
        securityDropdownVisible = false

        binding.layoutPassword.visibility =
            if (type == "None") View.GONE else View.VISIBLE
    }

}