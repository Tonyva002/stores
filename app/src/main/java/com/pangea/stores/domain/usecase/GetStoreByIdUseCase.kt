package com.pangea.stores.domain.usecase

import com.pangea.stores.domain.repository.StoreRepository
import javax.inject.Inject

class GetStoreByIdUseCase @Inject constructor(
    private val repository: StoreRepository
) {
    suspend operator fun invoke(id: Long) = repository.getStoreById(id)
}