package com.swx.dongzhou.pages.historyPage.statemachine

import com.swx.dongzhou.pages.historyPage.HistoryFragment

class HistoryStateMachine(
    private val fragment: HistoryFragment
) {
    var currentState: HistoryState? = null
        private set

    fun changeState(newState: HistoryState) {
        currentState?.exit(fragment)
        currentState = newState
        currentState?.enter(fragment)
    }
}
