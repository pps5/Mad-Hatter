package com.example.madhatter

import android.app.Application
import com.example.madhatter.core.di.MetroDi

class MadHatterApp : Application() {
    override fun onCreate() {
        super.onCreate()
        MetroDi.initialize(this)
    }
}
