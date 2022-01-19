package com.anonlatte.florarium.app

import android.app.Application
import coil.Coil
import coil.util.CoilUtils
import com.anonlatte.florarium.BuildConfig
import com.anonlatte.florarium.di.AppComponent
import com.anonlatte.florarium.di.DaggerAppComponent
import com.anonlatte.florarium.di.module.AppModule
import timber.log.Timber

class FlorariumApp : Application() {
    val appComponent: AppComponent = DaggerAppComponent.builder()
        .appModule(AppModule(this))
        .build()

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}