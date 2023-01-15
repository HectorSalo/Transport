package com.skysam.hchirinos.transport.repositories

import android.content.ContentValues
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.skysam.hchirinos.transport.R
import com.skysam.hchirinos.transport.common.Classes
import com.skysam.hchirinos.transport.common.Constants
import com.skysam.hchirinos.transport.common.Notifications
import com.skysam.hchirinos.transport.common.Transport
import com.skysam.hchirinos.transport.dataClasses.Booking
import com.skysam.hchirinos.transport.dataClasses.Payment
import com.skysam.hchirinos.transport.dataClasses.Refund
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.*

/**
 * Created by Hector Chirinos on 01/12/2022.
 */

object Bookings {
    private val PATH_BOOKINGS = when(Classes.getEnviroment()) {
        Constants.DEMO -> Constants.BOOKINGS_DEMO
        Constants.RELEASE -> Constants.BOOKINGS
        else -> Constants.BOOKINGS
    }

    private fun getInstance(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(PATH_BOOKINGS)
    }

    fun getBookings(): Flow<MutableList<Booking>> {
        return callbackFlow {
            val request = getInstance()
                .addSnapshotListener (MetadataChanges.INCLUDE) { value, error ->
                    if (error != null) {
                        Log.w(ContentValues.TAG, "Listen failed.", error)
                        return@addSnapshotListener
                    }

                    val bookings = mutableListOf<Booking>()
                    for (booking in value!!) {
                        val payments = mutableListOf<Payment>()
                        if (booking.get(Constants.PAYMENTS) != null) {
                            @Suppress("UNCHECKED_CAST")
                            val list = booking.data.getValue(Constants.PAYMENTS) as MutableList<HashMap<String, Any>>
                            for (item in list) {
                                val timestamp: Timestamp = item[Constants.DATE] as Timestamp
                                val date = timestamp.toDate()

                                val prod = Payment(
                                    item[Constants.PAYER].toString(),
                                    item[Constants.RECEIVER].toString(),
                                    date,
                                    item[Constants.AMOUNT].toString().toDouble()
                                )
                                payments.add(prod)
                            }
                        }
                        val refunds = mutableListOf<Refund>()
                        if (booking.get(Constants.REFUNDS) != null) {
                            @Suppress("UNCHECKED_CAST")
                            val list = booking.data.getValue(Constants.REFUNDS) as MutableList<HashMap<String, Any>>
                            for (item in list) {
                                val timestamp: Timestamp = item[Constants.DATE] as Timestamp
                                val date = timestamp.toDate()

                                val prod = Refund(
                                    item[Constants.PAYER].toString(),
                                    item[Constants.RECEIVER].toString(),
                                    date,
                                    item[Constants.AMOUNT].toString().toDouble()
                                )
                                refunds.add(prod)
                            }
                        }
                        val bookingNew = Booking(
                            booking.id,
                            booking.getString(Constants.NAME)!!,
                            booking.getDouble(Constants.QUANTITY)!!.toInt(),
                            booking.getDate(Constants.DATE)!!,
                            payments,
                            refunds
                        )
                        bookings.add(bookingNew)
                    }
                    trySend(Classes.organizedAlphabeticList(bookings))
                }
            awaitClose { request.remove() }
        }
    }

    fun addBooking(booking: Booking) {
        val data = hashMapOf(
            Constants.NAME to booking.name,
            Constants.DATE to booking.date,
            Constants.QUANTITY to booking.quantity,
            Constants.PAYMENTS to booking.payments,
            Constants.REFUNDS to booking.refunds
        )
        getInstance().add(data)
    }

    fun updateBooking(booking: Booking) {
        val data = hashMapOf(
            Constants.NAME to booking.name,
            Constants.DATE to booking.date,
            Constants.QUANTITY to booking.quantity,
            Constants.PAYMENTS to booking.payments,
            Constants.REFUNDS to booking.refunds
        )
        getInstance()
            .document(booking.id)
            .update(data)
    }

    fun addPayment(id: String, payment: Payment) {
        getInstance()
            .document(id)
            .update(Constants.PAYMENTS, FieldValue.arrayUnion(payment))
            .addOnSuccessListener {
                Notifications.sendNotification(
                    Transport.Transport.getContext().getString(R.string.notification_received_title),
                    Transport.Transport.getContext().getString(R.string.notification_received_message,
                        payment.payer, payment.amount.toString()),
                    Notifications.PATH_NOTIFICATION
                )
            }
    }

    fun addRefund(id: String, refund: Refund) {
        getInstance()
            .document(id)
            .update(Constants.REFUNDS, FieldValue.arrayUnion(refund))
    }

    fun deleteBooking(id: String) {
        getInstance()
            .document(id)
            .delete()
    }
}