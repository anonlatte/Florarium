package com.anonlatte.florarium.app

import android.app.Application
import com.anonlatte.florarium.di.DaggerAppComponent
import com.anonlatte.florarium.di.module.AppModule

class FlorariumApp : Application() {
    val appComponent = DaggerAppComponent.builder()
        .appModule(AppModule(this))
        .build()
}