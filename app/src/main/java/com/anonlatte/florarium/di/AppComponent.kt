package com.anonlatte.florarium.di

import com.anonlatte.florarium.di.module.AppModule
import com.anonlatte.florarium.di.module.BindingsModule
import com.anonlatte.florarium.di.module.DatabaseModule
import com.anonlatte.florarium.ui.creation.CreationFragment
import com.anonlatte.florarium.ui.home.HomeFragment
import dagger.Component

@Component(modules = [AppModule::class, DatabaseModule::class, BindingsModule::class])
interface AppComponent {
    fun inject(fragment: HomeFragment)
    fun inject(fragment: CreationFragment)
}