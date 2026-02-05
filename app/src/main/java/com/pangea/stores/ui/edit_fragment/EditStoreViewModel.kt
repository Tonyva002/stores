package com.pangea.stores.ui.edit_fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pangea.stores.R
import com.pangea.stores.domain.model.Store
import com.pangea.stores.domain.usecase.AddStoreUseCase
import com.pangea.stores.domain.usecase.GetStoreByIdUseCase
import com.pangea.stores.domain.usecase.UpdateStoreUseCase
import com.pangea.stores.ui.states.edit_fragment_state.EditStoreEvent
import com.pangea.stores.ui.states.edit_fragment_state.EditStoreUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditStoreViewModel @Inject constructor(
    private val addStore: AddStoreUseCase,
    private val updateStore: UpdateStoreUseCase,
    private val getStoreById: GetStoreByIdUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<EditStoreUiState>(EditStoreUiState.Idle)
    val uiState: StateFlow<EditStoreUiState> = _uiState

    private val _events = MutableSharedFlow<EditStoreEvent>()
    val events = _events.asSharedFlow()


    fun loadStore(id: Long) {
        viewModelScope.launch {
            _uiState.value = EditStoreUiState.Loading
            try {
                val store = getStoreById(id)
                _uiState.value = EditStoreUiState.Success(store)
            } catch (e: Exception) {
                _uiState.value = EditStoreUiState.Error(
                    e.message ?: "Error loading store"
                )

                _events.emit(
                    EditStoreEvent.ShowMessage(
                        R.string.error_loading_store
                    )
                )

            }
        }

    }

    fun saveStore(original: Store, updated: Store) {
        viewModelScope.launch {
            val isEditMode = original.id != 0L

            if (isEditMode && !hasChanges(original, updated)){
                _events.emit(EditStoreEvent.ShowMessage(
                    R.string.no_changes_detected
                )
                )
                return@launch
            }
            _uiState.value = EditStoreUiState.Loading

            try {
                if (isEditMode) {
                    updateStore(updated)
                    _events.emit(EditStoreEvent.Updated)
                } else {
                    addStore(updated)
                    _events.emit(EditStoreEvent.Created)
                }
            } catch (e: Exception) {
                _uiState.value = EditStoreUiState.Error(
                    e.message ?: "Error saving store"
                )
               _events.emit(EditStoreEvent.ShowMessage(
                   R.string.error_saving_store
               ))
            }
        }
    }

    private fun hasChanges(original: Store, updated: Store): Boolean {
        return original != updated
    }


}