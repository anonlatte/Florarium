package com.anonlatte.florarium.extensions

import android.content.Context
import com.anonlatte.florarium.app.FlorariumApp
import com.anonlatte.florarium.di.AppComponent

val Context.appComponent: AppComponent get() = (applicationContext as FlorariumApp).appComponent