package com.anonlatte.florarium.di.module

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataStoreModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        val dataStore: DataStore<Preferences> = PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile(DATA_STORE_FILE_NAME)
        }
        return dataStore
    }

    companion object {
        private const val DATA_STORE_FILE_NAME = "preferences"
    }
}