package com.skysam.hchirinos.transport.ui.bookings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.skysam.hchirinos.transport.common.Preferences
import com.skysam.hchirinos.transport.dataClasses.Booking
import com.skysam.hchirinos.transport.dataClasses.Payment
import com.skysam.hchirinos.transport.dataClasses.Refund
import com.skysam.hchirinos.transport.repositories.Bookings
import java.util.Locale

class BookingViewModel : ViewModel() {
    val bookings: LiveData<MutableList<Booking>> = Bookings.getBookings().asLiveData()
    val assemblyActive: LiveData<Boolean> = Preferences.getAssemblyStatus().asLiveData()

    private val _bookingsFiltred = MutableLiveData<MutableList<Booking>>().apply {
        value = mutableListOf()
    }
    val bookingsFiltred: LiveData<MutableList<Booking>> get() = _bookingsFiltred

    private val _bookingToView = MutableLiveData<Booking>()
    val bookingToView: LiveData<Booking> get() = _bookingToView

    fun filter(text: String, allContacts: List<Booking>) {
        _bookingsFiltred.value?.clear()
        // looping through existing names
        if(text.isNotEmpty()){
            for (contact in allContacts) {
                if (contact.name.lowercase(Locale.ROOT).contains(text.lowercase(
                        Locale.ROOT))) {
                    _bookingsFiltred.value?.add(contact)
                }
            }
        } else {
            _bookingsFiltred.value?.addAll(allContacts)
        }
        _bookingsFiltred.value = _bookingsFiltred.value
    }

    fun addBooking(booking: Booking) {
        Bookings.addBooking(booking)
    }

    fun deleteBooking(booking: Booking) {
        Bookings.deleteBooking(booking.id)
    }

    fun viewBooking(booking: Booking) {
        _bookingToView.value = booking
    }

    fun addPayment(booking: Booking, payment: Payment) {
        Bookings.addPayment(booking.id, payment)
    }

    fun addRefund(booking: Booking, refund: Refund) {
        Bookings.addRefund(booking.id, refund)
    }

    fun updateBooking(booking: Booking) {
        Bookings.updateBooking(booking)
    }
}