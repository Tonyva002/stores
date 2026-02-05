package com.pangea.stores.domain.usecase

import com.pangea.stores.domain.model.Store
import com.pangea.stores.domain.repository.StoreRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStoresUseCase @Inject constructor(private val repository: StoreRepository) {
    operator fun invoke(): Flow<List<Store>> = repository.getStores()
}