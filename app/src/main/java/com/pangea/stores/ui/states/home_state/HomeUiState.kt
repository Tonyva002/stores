package com.pangea.stores.ui.states.home_state

import com.pangea.stores.domain.model.Store

sealed class HomeUiState {

    object Loading : HomeUiState()

    data class Success(val stores: List<Store>) : HomeUiState()

    data class Error(val message: String) : HomeUiState()
}