package com.anonlatte.florarium.di.module

import com.anonlatte.florarium.data.repository.IMainRepository
import com.anonlatte.florarium.data.repository.MainRepository
import dagger.Binds
import dagger.Module

@Suppress("unused")
@Module
abstract class BindingsModule {
    @Binds
    abstract fun bindMainRepository(mainRepository: MainRepository): IMainRepository
}