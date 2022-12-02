package com.skysam.hchirinos.transport.common

import android.content.ContentValues
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Created by Hector Chirinos on 30/11/2022.
 */

object InitSession {
 private fun getInstance(): FirebaseAuth {
  return FirebaseAuth.getInstance()
 }

 fun getUser(): FirebaseUser? {
  return getInstance().currentUser
 }

 fun initSession(): Flow<String?> {
  return callbackFlow {
   getInstance().signInWithEmailAndPassword(Constants.EMAIL_INIT, Constants.PASS_INIT)
    .addOnCompleteListener { task ->
     if (task.isSuccessful) {
      trySend(Constants.OK)
     } else {
      Log.w(ContentValues.TAG, "signInWithEmail:failure", task.exception)
      trySend(task.exception?.localizedMessage)
     }
    }
   awaitClose {  }
  }
 }
}