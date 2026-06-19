package com.swx.dongzhou.pages.historyPage

import androidx.annotation.DrawableRes

data class HistoryGroupItem(
    val title: String,
    val records: List<HistoryRecordItem>,
    val showViewAll: Boolean = false
)

data class HistoryRecordItem(
    val title: String,
    val type: String,
    @DrawableRes val iconRes: Int,
    val time: String? = null,
    val isFavorite: Boolean = false
)
