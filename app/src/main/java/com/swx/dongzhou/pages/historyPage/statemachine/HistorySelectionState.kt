package com.swx.dongzhou.pages.historyPage.statemachine

import com.swx.dongzhou.pages.historyPage.HistoryFragment

class HistorySelectionState(
    override val contentMode: HistoryContentMode = HistoryContentMode.ALL
) : HistoryState() {

    override fun enter(fragment: HistoryFragment) {
        fragment.updateHistoryMode()
    }

    override fun exit(fragment: HistoryFragment) {
        fragment.clearSelection()
    }
}
