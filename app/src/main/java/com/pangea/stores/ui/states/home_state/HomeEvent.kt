package com.pangea.stores.ui.states.home_state

sealed class HomeEvent {
    data class ShowMessage(val resId: Int) : HomeEvent()
}