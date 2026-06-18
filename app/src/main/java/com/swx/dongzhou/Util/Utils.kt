package com.swx.dongzhou.Util

import com.swx.dongzhou.R

object Utils {
    fun getItemImage(type: QRCodeType): Int{
        return when(type){
            QRCodeType.Website -> R.mipmap.ic_url
            QRCodeType.WIFI -> R.mipmap.ic_wifi
            QRCodeType.Text -> R.mipmap.ic_text
            QRCodeType.Contact -> R.mipmap.ic_contact
            QRCodeType.Tel -> R.mipmap.ic_tel
            QRCodeType.Email -> R.mipmap.ic_email
            QRCodeType.SMS -> R.mipmap.ic_sms
            QRCodeType.Calendar -> R.mipmap.ic_calendar
            QRCodeType.MyCard -> R.mipmap.ic_mecard
            QRCodeType.FaceBook -> R.mipmap.ic_facebook
            QRCodeType.Instagram -> R.mipmap.ic_ins
            QRCodeType.WhatsApp -> R.mipmap.ic_whatsapp
            QRCodeType.Youtube -> R.mipmap.ic_youtobe
            QRCodeType.Twitter -> R.mipmap.ic_twitter
            QRCodeType.Spotify -> R.mipmap.ic_spotify
            QRCodeType.Paypal -> R.mipmap.ic_paypal
            QRCodeType.Viber -> R.mipmap.ic_viber
        }
    }
}