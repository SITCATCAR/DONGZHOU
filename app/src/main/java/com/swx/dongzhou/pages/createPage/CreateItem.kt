package com.swx.dongzhou.pages.createPage

enum class CreateItemType{
    Website, WIFI, Text,
    Contact, Tel, Email,
    SMS,Calendar,MyCard,
    FaceBook,Instagram,WhatsApp,
    Youtube,Twitter,Spotify,
    Paypal,Viber

}

data class CreateItem(val name: String,val type: CreateItemType) {
}