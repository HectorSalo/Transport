package com.skysam.hchirinos.transport.common

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.skysam.hchirinos.transport.dataClasses.Bus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Created by Hector Chirinos on 30/11/2022.
 */

object Preferences {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(Constants.PREFERENCES)
    private val sharedPref = Transport.Transport.getContext().getSharedPreferences(
        Constants.PREFERENCES, Context.MODE_PRIVATE)

    private val PREFERENCE_NOTIFICATION = booleanPreferencesKey(Constants.PREFERENCES_NOTIFICATION)
    private val PREFERENCE_ASSEMBLY = booleanPreferencesKey(Constants.PREFERENCES_ASSEMBLY)

    fun getNotificationStatus(): Flow<Boolean> {
        return Transport.Transport.getContext().dataStore.data
            .map {
                it[PREFERENCE_NOTIFICATION] ?: true
            }
    }

    fun getAssemblyStatus(): Flow<Boolean> {
        return Transport.Transport.getContext().dataStore.data
            .map {
                it[PREFERENCE_ASSEMBLY] ?: false
            }
    }

    suspend fun changeNotificationStatus(status: Boolean) {
        Transport.Transport.getContext().dataStore.edit {
            it[PREFERENCE_NOTIFICATION] = status
        }
    }

    suspend fun changeAssemblyStatus(status: Boolean) {
        Transport.Transport.getContext().dataStore.edit {
            it[PREFERENCE_ASSEMBLY] = status
        }
    }

    fun changePriceSeat(bus: Bus) {
        with (sharedPref.edit()) {
            putFloat(Constants.PRICE, (bus.price / bus.quantity).toFloat())
            apply()
        }
    }

    fun getPriceSeat(): Double {
        return sharedPref.getFloat(Constants.PRICE, 0.0f).toDouble()
    }
}