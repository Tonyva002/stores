package com.pangea.stores.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pangea.stores.data.local.dao.StoreDao
import com.pangea.stores.data.local.entity.StoreEntity

@Database(
    entities = [StoreEntity::class],
    version = 1,
    exportSchema = false
)
abstract class StoreDatabase: RoomDatabase(){
    abstract fun storeDao(): StoreDao
}