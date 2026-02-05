package com.pangea.stores.data.repository

import com.pangea.stores.data.local.dao.StoreDao
import com.pangea.stores.data.mapper.toDomain
import com.pangea.stores.data.mapper.toEntity
import com.pangea.stores.domain.model.Store
import com.pangea.stores.domain.repository.StoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class StoreRepositoryImpl @Inject constructor(
    private val dao: StoreDao
) : StoreRepository {
    override fun getStores(): Flow<List<Store>> =
        dao.getAllStores().map { list -> list.map { it.toDomain() } }


    override suspend fun getStoreById(id: Long): Store =
        dao.getStoreById(id).toDomain()


    override suspend fun addStore(store: Store): Long =
        dao.addStore(store.toEntity())


    override suspend fun updateStore(store: Store) {
        dao.updateStore(store.toEntity())
    }

    override suspend fun deleteStore(store: Store) {
        dao.deleteStore(store.toEntity())
    }
}