package com.skysam.hchirinos.transport.common

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Created by Hector Chirinos on 30/11/2022.
 */

object Preferences {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(Constants.PREFERENCES)

    private val PREFERENCE_NOTIFICATION = booleanPreferencesKey(Constants.PREFERENCES_NOTIFICATION)

    fun getNotificationStatus(): Flow<Boolean> {
        return Transport.Transport.getContext().dataStore.data
            .map {
                it[PREFERENCE_NOTIFICATION] ?: true
            }
    }

    suspend fun changeNotificationStatus(status: Boolean) {
        Transport.Transport.getContext().dataStore.edit {
            it[PREFERENCE_NOTIFICATION] = status
        }
    }
}