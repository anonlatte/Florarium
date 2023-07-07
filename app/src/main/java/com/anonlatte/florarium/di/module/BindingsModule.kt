package com.anonlatte.florarium.di.module

import com.anonlatte.florarium.data.repository.IMainRepository
import com.anonlatte.florarium.data.repository.MainRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class BindingsModule {
    @Binds
    @Singleton
    abstract fun bindMainRepository(mainRepository: MainRepository): IMainRepository
}