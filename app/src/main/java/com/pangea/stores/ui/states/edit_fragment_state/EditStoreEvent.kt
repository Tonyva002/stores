package com.pangea.stores.ui.states.edit_fragment_state

sealed class EditStoreEvent {
    data class ShowMessage(val resId: Int) : EditStoreEvent()
    object Created : EditStoreEvent()
    object Updated : EditStoreEvent()
}