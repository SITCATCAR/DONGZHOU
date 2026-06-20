package com.swx.dongzhou.HistoryDatabase

import androidx.room.TypeConverter
import com.swx.dongzhou.Util.HistorySource
import com.swx.dongzhou.Util.QRCodeType

class HistoryConverters {

    @TypeConverter
    fun fromQRCodeType(type: QRCodeType): String{
        return type.name
    }

    @TypeConverter
    fun toQRCodeType(value: String): QRCodeType{
        return QRCodeType.valueOf(value)
    }

    @TypeConverter
    fun fromHistorySource(type: HistorySource): String{
        return type.name
    }

    @TypeConverter
    fun toHistorySource(value: String): HistorySource{
        return HistorySource.valueOf(value)
    }
}
