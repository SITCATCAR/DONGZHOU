package com.swx.dongzhou.pages.createPage

import androidx.recyclerview.widget.GridLayoutManager
import com.swx.dongzhou.BaseFragment
import com.swx.dongzhou.R
import com.swx.dongzhou.Util.QRCodeType
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
        list.add(CreateItem(getString(R.string.website), QRCodeType.Website))
        list.add(CreateItem("WIFI", QRCodeType.WIFI))
        list.add(CreateItem(getString(R.string.text), QRCodeType.Text))
        list.add(CreateItem(getString(R.string.contact), QRCodeType.Contact))
        list.add(CreateItem(getString(R.string.tel), QRCodeType.Tel))
        list.add(CreateItem("E-mail", QRCodeType.Email))
        list.add(CreateItem(getString(R.string.sms), QRCodeType.SMS))
        list.add(CreateItem(getString(R.string.calendar), QRCodeType.Calendar))
        list.add(CreateItem(getString(R.string.mycard), QRCodeType.MyCard))
        list.add(CreateItem("FaceBook", QRCodeType.FaceBook))
        list.add(CreateItem("Instagram", QRCodeType.Instagram))
        list.add(CreateItem("WhatsApp", QRCodeType.WhatsApp))
        list.add(CreateItem("YouTube", QRCodeType.Youtube))
        list.add(CreateItem("Twitter", QRCodeType.Twitter))
        list.add(CreateItem("Spotify", QRCodeType.Spotify))
        list.add(CreateItem("PayPal", QRCodeType.Paypal))
        list.add(CreateItem("Viber", QRCodeType.Viber))
    }
}