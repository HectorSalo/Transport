package com.skysam.hchirinos.transport.repositories

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.skysam.hchirinos.transport.R
import com.skysam.hchirinos.transport.common.Constants
import com.skysam.hchirinos.transport.common.Transport
import com.skysam.hchirinos.transport.dataClasses.InfoApp
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Created by Hector Chirinos on 20/01/2024.
 */

object InfoApp {
 private fun getInstance(): CollectionReference {
  return FirebaseFirestore.getInstance().collection(Constants.INFO_APP)
 }

 fun getInfoApp(): Flow<InfoApp> {
  return callbackFlow {
   val request = getInstance()
    .document(Transport.Transport.getContext().getString(R.string.info_app))
    .addSnapshotListener { value, error ->
     if (error != null) {
      Log.w(ContentValues.TAG, "Listen failed.", error)
      return@addSnapshotListener
     }

     if (value != null && value.exists()) {
      val infoApp = InfoApp(
       value.getDouble(Constants.VERSION_CODE)!!.toInt(),
       value.getString(Constants.VERSION_NAME)!!
      )
      trySend(infoApp)
     } else {
      Log.d(ContentValues.TAG, "Current data: null")
     }
    }
   awaitClose { request.remove() }
  }
 }
}