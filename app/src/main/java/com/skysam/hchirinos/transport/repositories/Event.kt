package com.skysam.hchirinos.transport.repositories

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.skysam.hchirinos.transport.common.Constants
import com.skysam.hchirinos.transport.dataClasses.Bus
import com.skysam.hchirinos.transport.dataClasses.Event
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

object Event {
    private fun getInstance(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(Constants.INFO_APP)
    }

    fun getEvent(): Flow<Event> {
        return callbackFlow {
            val request = getInstance()
                .document(Constants.EVENT)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Log.w(ContentValues.TAG, "Listen failed.", error)
                        return@addSnapshotListener
                    }

                    if (value != null && value.exists()) {
                        val event = Event(
                            value.id,
                            value.getString(Constants.TITLE)!!,
                            value.getString(Constants.IMAGE)!!,
                            value.getBoolean(Constants.IS_REGIONAL)!!,
                            value.getDate(Constants.DATE)!!
                        )
                        trySend(event)
                    }
                }
            awaitClose { request.remove() }
        }
    }
}