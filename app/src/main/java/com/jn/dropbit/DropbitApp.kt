package com.jn.dropbit

import android.app.Application
import com.jn.dropbit.di.AppContainer

class DropbitApp : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
