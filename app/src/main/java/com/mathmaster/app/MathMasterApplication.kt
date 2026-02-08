package com.mathmaster.app

import android.app.Application
import com.mathmaster.app.di.AppContainer

class MathMasterApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
