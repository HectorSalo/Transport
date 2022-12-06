package com.skysam.hchirinos.transport.common

import android.app.Application
import android.content.Context
import androidx.lifecycle.asLiveData
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

/**
 * Created by Hector Chirinos on 30/11/2022.
 */

class Transport: Application() {
    companion object {
        private lateinit var mRequestQueue: RequestQueue
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        Preferences.getNotificationStatus().asLiveData().observeForever {
            if (it) Notifications.subscribeToNotifications()
            else Notifications.unsubscribeToNotifications()
        }
    }

    object Transport {
        fun getContext(): Context = appContext

        private fun getmRequestQueue(): RequestQueue {
            mRequestQueue = Volley.newRequestQueue(appContext)
            return mRequestQueue
        }

        fun <T> addToReqQueue(request: Request<T>) {
            request.retryPolicy =
                DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            getmRequestQueue().add(request)
        }
    }
}