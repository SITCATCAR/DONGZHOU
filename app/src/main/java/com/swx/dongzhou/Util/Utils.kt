package com.swx.dongzhou.Util

import com.swx.dongzhou.R
import com.swx.dongzhou.pages.createPage.CreateItem
import com.swx.dongzhou.pages.createPage.CreateItemType

object Utils {
    fun getItemImage(type: CreateItemType): Int{
        return when(type){
            CreateItemType.Website -> R.mipmap.ic_url
            CreateItemType.WIFI -> R.mipmap.ic_wifi
            CreateItemType.Text -> R.mipmap.ic_text
            CreateItemType.Contact -> R.mipmap.ic_contact
            CreateItemType.Tel -> R.mipmap.ic_tel
            CreateItemType.Email -> R.mipmap.ic_email
            CreateItemType.SMS -> R.mipmap.ic_sms
            CreateItemType.Calendar -> R.mipmap.ic_calendar
            CreateItemType.MyCard -> R.mipmap.ic_mecard
            CreateItemType.FaceBook -> R.mipmap.ic_facebook
            CreateItemType.Instagram -> R.mipmap.ic_ins
            CreateItemType.WhatsApp -> R.mipmap.ic_whatsapp
            CreateItemType.Youtube -> R.mipmap.ic_youtobe
            CreateItemType.Twitter -> R.mipmap.ic_twitter
            CreateItemType.Spotify -> R.mipmap.ic_spotify
            CreateItemType.Paypal -> R.mipmap.ic_paypal
            CreateItemType.Viber -> R.mipmap.ic_viber
        }
    }
}