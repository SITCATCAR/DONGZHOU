package com.swx.dongzhou.pages.historyPage

import androidx.annotation.DrawableRes
import com.swx.dongzhou.Util.QRCodeType

data class HistoryGroupItem(
    val title: String,
    val records: List<HistoryRecordItem>,
    val showViewAll: Boolean = false
)

data class HistoryRecordItem(
    val id: Long,
    val title: String,
    val content: String,
    val type: String,
    val qrCodeType: QRCodeType,
    @param:DrawableRes val iconRes: Int,
    val time: String? = null,
    val isFavorite: Boolean = false,
    val showFavoriteIcon: Boolean = false,
    val isSelected: Boolean = false,
    val isSelectionMode: Boolean = false
)
