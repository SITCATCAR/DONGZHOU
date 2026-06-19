package com.swx.dongzhou.pages.historyPage

import androidx.recyclerview.widget.LinearLayoutManager
import com.swx.dongzhou.BaseFragment
import com.swx.dongzhou.R
import com.swx.dongzhou.databinding.HistoryFragmentBinding

class HistoryFragment : BaseFragment<HistoryFragmentBinding>(
    HistoryFragmentBinding::inflate
){
    private val historyList = mutableListOf<HistoryGroupItem>()
    private lateinit var historyAdapter: HistoryAdapter

    override fun initView() {
        historyAdapter = HistoryAdapter(historyList)
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.historyRecyclerView.adapter = historyAdapter
    }

    override fun loadData() {
        historyList.clear()
        historyList.addAll(getDefaultHistoryList())
    }

    private fun getDefaultHistoryList(): List<HistoryGroupItem> {
        return listOf(
            HistoryGroupItem(
                title = "Favorites",
                records = listOf(
                    HistoryRecordItem(
                        title = "https://fonts.google.comlec.c...",
                        type = "URL",
                        iconRes = R.mipmap.ic_url,
                        isFavorite = true
                    ),
                    HistoryRecordItem(
                        title = "I pay for the meal with a credit...",
                        type = "Text",
                        iconRes = R.mipmap.ic_text,
                        isFavorite = true
                    ),
                    HistoryRecordItem(
                        title = "Huaxing Century Building, Wan...",
                        type = "Location",
                        iconRes = R.mipmap.ic_create_address,
                        isFavorite = true
                    )
                ),
                showViewAll = true
            ),
            HistoryGroupItem(
                title = "Today",
                records = listOf(
                    HistoryRecordItem(
                        title = "https://fonts.google.comlec.c...",
                        type = "URL",
                        iconRes = R.mipmap.ic_url,
                        time = "16:28"
                    ),
                    HistoryRecordItem(
                        title = "I pay for the meal with a credit...",
                        type = "Text",
                        iconRes = R.mipmap.ic_text,
                        time = "12:28"
                    ),
                    HistoryRecordItem(
                        title = "Huaxing Century Building, Wan...",
                        type = "Location",
                        iconRes = R.mipmap.ic_create_address,
                        time = "06:28"
                    )
                )
            ),
            HistoryGroupItem(
                title = "Dec 27, 2020",
                records = listOf(
                    HistoryRecordItem(
                        title = "www.youtube.com",
                        type = "Youtube",
                        iconRes = R.mipmap.ic_youtobe,
                        time = "15:22"
                    ),
                    HistoryRecordItem(
                        title = "https://www.instagram.com/?hl...",
                        type = "Instagram",
                        iconRes = R.mipmap.ic_ins,
                        time = "12:28"
                    )
                )
            )
        )
    }
}
