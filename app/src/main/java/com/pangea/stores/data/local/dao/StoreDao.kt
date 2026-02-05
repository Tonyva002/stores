package com.pangea.stores.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.pangea.stores.data.local.entity.StoreEntity
import kotlinx.coroutines.flow.Flow

    @Dao
    interface StoreDao {

        @Query("SELECT * FROM store ORDER BY name ASC")
        fun getAllStores(): Flow<List<StoreEntity>>

        @Query("SELECT * FROM store WHERE id = :id")
        suspend fun getStoreById(id: Long): StoreEntity


        @Insert
        suspend fun addStore(storeEntity: StoreEntity): Long

        @Update
        suspend fun updateStore(storeEntity: StoreEntity)

        @Delete
        suspend fun deleteStore(storeEntity: StoreEntity)
    }
