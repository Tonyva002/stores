package com.pangea.stores.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pangea.stores.R
import com.pangea.stores.domain.model.Store
import com.pangea.stores.domain.usecase.DeleteStoreUseCase
import com.pangea.stores.domain.usecase.GetStoresUseCase
import com.pangea.stores.domain.usecase.UpdateStoreUseCase
import com.pangea.stores.ui.states.home_state.HomeEvent
import com.pangea.stores.ui.states.home_state.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getStores: GetStoresUseCase,
    private val updateStore: UpdateStoreUseCase,
    private val deleteStore: DeleteStoreUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    private val _events = MutableSharedFlow<HomeEvent>()
    val events = _events.asSharedFlow()

    init {
        observeStores()
    }

    private fun observeStores() {
        viewModelScope.launch {
            getStores()
                .catch { e ->
                    _uiState.value = HomeUiState.Error(e.message ?: "Error loading stores")

                    _events.emit(HomeEvent.ShowMessage(R.string.error_loading_store))
                }
                .collect { stores ->
                    _uiState.value = HomeUiState.Success(stores)
                }
        }
    }

    fun toggleFavorite(store: Store) {
        viewModelScope.launch {
            try {
                updateStore(store.copy(isFavorite = !store.isFavorite))
            } catch (e: Exception) {
                    _events.emit(HomeEvent.ShowMessage(R.string.error_adding_to_favorites))
            }
        }
    }

    fun delete(store: Store) {
        viewModelScope.launch {
            try {
                deleteStore(store)
            } catch (e: Exception) {
                _events.emit(HomeEvent.ShowMessage(R.string.error_deleting_store))
            }
        }
    }
}