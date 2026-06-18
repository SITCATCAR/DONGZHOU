package com.swx.dongzhou.HistoryDatabase

import androidx.room.TypeConverters
import com.swx.dongzhou.Util.HistorySource
import com.swx.dongzhou.Util.QRCodeType

class HistoryConverters {

    @TypeConverters
    fun fromQRCodeType(type: QRCodeType): String{
        return type.name
    }

    @TypeConverters
    fun toQRCodeType(value: String): QRCodeType{
        return QRCodeType.valueOf(value)
    }

    @TypeConverters
    fun fromHistorySource(type: HistorySource): String{
        return type.name
    }

    @TypeConverters
    fun toHistorySource(value: String): HistorySource{
        return HistorySource.valueOf(value)
    }
}