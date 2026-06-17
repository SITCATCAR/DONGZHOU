package com.swx.dongzhou.pages.createPage

import androidx.recyclerview.widget.GridLayoutManager
import com.swx.dongzhou.BaseFragment
import com.swx.dongzhou.R
import com.swx.dongzhou.databinding.CreateFragmentBinding

class CreateFragment: BaseFragment<CreateFragmentBinding>(CreateFragmentBinding::inflate) {

    val list = mutableListOf<CreateItem>()

    override fun initView() {
        addItems()
        binding.RCView.layoutManager= GridLayoutManager(context,3)
        binding.RCView.adapter= CreateAdapter(activity,list)


    }

    override fun loadData() {

    }

    fun addItems(){
        list.add(CreateItem(getString(R.string.website), CreateItemType.Website))
        list.add(CreateItem("WIFI", CreateItemType.WIFI))
        list.add(CreateItem(getString(R.string.text), CreateItemType.Text))
        list.add(CreateItem(getString(R.string.contact), CreateItemType.Contact))
        list.add(CreateItem(getString(R.string.tel), CreateItemType.Tel))
        list.add(CreateItem("E-mail", CreateItemType.Email))
        list.add(CreateItem(getString(R.string.sms), CreateItemType.SMS))
        list.add(CreateItem(getString(R.string.calendar), CreateItemType.Calendar))
        list.add(CreateItem(getString(R.string.mycard), CreateItemType.MyCard))
        list.add(CreateItem("FaceBook", CreateItemType.FaceBook))
        list.add(CreateItem("Instagram", CreateItemType.Instagram))
        list.add(CreateItem("WhatsApp", CreateItemType.WhatsApp))
        list.add(CreateItem("YouTube", CreateItemType.Youtube))
        list.add(CreateItem("Twitter", CreateItemType.Twitter))
        list.add(CreateItem("Spotify", CreateItemType.Spotify))
        list.add(CreateItem("PayPal", CreateItemType.Paypal))
        list.add(CreateItem("Viber", CreateItemType.Viber))
    }
}