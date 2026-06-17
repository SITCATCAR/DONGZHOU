package com.swx.dongzhou.Activities.CreateActivities

import com.swx.dongzhou.BaseActivity
import com.swx.dongzhou.R
import com.swx.dongzhou.databinding.ActivityFacebookCreateBinding

class FaceBookCreateActivity: BaseActivity<ActivityFacebookCreateBinding>(
    ActivityFacebookCreateBinding::inflate
){
    override fun initView() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }



}