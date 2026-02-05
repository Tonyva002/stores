package com.pangea.stores.ui.states.edit_fragment_state

import com.pangea.stores.domain.model.Store

sealed class EditStoreUiState {

    object Idle : EditStoreUiState()

    object Loading : EditStoreUiState()

    data class Success(val store: Store) : EditStoreUiState()

    data class Error(val message: String) : EditStoreUiState()
}