package com.swx.dongzhou.pages.historyPage.statemachine

import com.swx.dongzhou.pages.historyPage.HistoryFragment

abstract class HistoryState {

    abstract val contentMode: HistoryContentMode

    open fun enter(fragment: HistoryFragment) {}

    open fun exit(fragment: HistoryFragment) {}
}
