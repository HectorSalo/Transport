package com.skysam.hchirinos.transport.common

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

object Permission {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun checkPermissionNotification(): Boolean {
        val result = ContextCompat.checkSelfPermission(Transport.Transport.getContext(), Manifest.permission.POST_NOTIFICATIONS)
        return result == PackageManager.PERMISSION_GRANTED
    }
}