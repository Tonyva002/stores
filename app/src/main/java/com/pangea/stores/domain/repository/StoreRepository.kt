package com.pangea.stores.domain.repository

import com.pangea.stores.domain.model.Store
import kotlinx.coroutines.flow.Flow

interface StoreRepository {
    fun getStores(): Flow<List<Store>>
    suspend fun getStoreById(id: Long): Store
    suspend fun addStore(store: Store): Long
    suspend fun updateStore(store: Store)
    suspend fun deleteStore(store: Store)
}