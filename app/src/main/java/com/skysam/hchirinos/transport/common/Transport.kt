package com.skysam.hchirinos.transport.common

import android.app.Application
import android.content.Context

/**
 * Created by Hector Chirinos on 30/11/2022.
 */

class Transport: Application() {
    companion object {
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

    object Transport {
        fun getContext(): Context = appContext
    }
}