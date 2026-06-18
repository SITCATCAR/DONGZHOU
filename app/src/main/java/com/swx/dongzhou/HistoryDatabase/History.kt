package com.swx.dongzhou.HistoryDatabase

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.swx.dongzhou.Util.QRCodeType

@Entity(tableName = "history")
data class History(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val title: String,

    val content: String,

    val type: QRCodeType,

    val isFavorite: Boolean = false,

    val createdAt: Long = System.currentTimeMillis(),

    val favoriteAt: Long? = null,

//    val source: HistorySource = HistorySource.SCAN
)