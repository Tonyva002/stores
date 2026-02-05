package com.pangea.stores.domain.usecase

import com.pangea.stores.domain.model.Store
import com.pangea.stores.domain.repository.StoreRepository
import javax.inject.Inject

class UpdateStoreUseCase @Inject constructor(
    private val repository: StoreRepository
) {
    suspend operator fun invoke(store: Store) = repository.updateStore(store)
}