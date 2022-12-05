package com.skysam.hchirinos.transport.repositories

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.skysam.hchirinos.transport.common.Constants
import com.skysam.hchirinos.transport.dataClasses.Bus
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Created by Hector Chirinos on 05/12/2022.
 */

object Busses {
    private fun getInstance(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(Constants.BUS)
    }

    fun getBus(): Flow<Bus> {
        return callbackFlow {
            val request = getInstance()
                .document(Constants.BUS)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Log.w(ContentValues.TAG, "Listen failed.", error)
                        return@addSnapshotListener
                    }

                    if (value != null && value.exists()) {
                        val bus = Bus(
                            value.id,
                            value.getDouble(Constants.QUANTITY)!!.toInt(),
                            value.getDouble(Constants.PRICE)!!
                            )
                        trySend(bus)
                    }
                }
            awaitClose { request.remove() }
        }
    }

}