package com.skysam.hchirinos.transport.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.skysam.hchirinos.transport.common.Notifications
import com.skysam.hchirinos.transport.common.Preferences

class SettingsViewModel : ViewModel() {

    val notificationActive: LiveData<Boolean> = Preferences.getNotificationStatus().asLiveData()

    suspend fun changeNotificationStatus(status: Boolean) {
        Preferences.changeNotificationStatus(status)
        if (status) Notifications.subscribeToNotifications()
        else Notifications.unsubscribeToNotifications()
    }
}