package com.swx.dongzhou.pages.historyPage.statemachine

import com.swx.dongzhou.pages.historyPage.HistoryFragment

class HistoryNormalState(
    override val contentMode: HistoryContentMode = HistoryContentMode.ALL
) : HistoryState() {

    override fun enter(fragment: HistoryFragment) {
        fragment.clearSelection()
        fragment.updateHistoryMode()
    }
}
