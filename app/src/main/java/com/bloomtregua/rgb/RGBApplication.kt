package com.bloomtregua.rgb

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RGBApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}