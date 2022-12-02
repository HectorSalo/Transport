package com.skysam.hchirinos.transport.common

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.skysam.hchirinos.transport.common.Constants

/**
 * Created by Hector Chirinos on 30/11/2022.
 */

object Preference {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(Constants.PREFERENCES)

}