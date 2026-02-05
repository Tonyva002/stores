package com.pangea.stores.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.pangea.stores.data.local.dao.StoreDao
import com.pangea.stores.data.local.database.StoreDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE StoreEntity ADD COLUMN photoUrl TEXT NOT NULL DEFAULT ''")
        }
    }

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): StoreDatabase = Room.databaseBuilder(
        context,
        StoreDatabase::class.java,
        "store_db"
    )
        .addMigrations(MIGRATION_1_2)
        .build()

    @Provides
    fun provideStoreDao(db: StoreDatabase): StoreDao = db.storeDao()
}