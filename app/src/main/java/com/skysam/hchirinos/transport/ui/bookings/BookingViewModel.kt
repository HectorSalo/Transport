package com.skysam.hchirinos.transport.ui.bookings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.skysam.hchirinos.transport.dataClasses.Booking
import com.skysam.hchirinos.transport.dataClasses.Payment
import com.skysam.hchirinos.transport.dataClasses.Refund
import com.skysam.hchirinos.transport.repositories.Bookings

class BookingViewModel : ViewModel() {
    val bookings: LiveData<MutableList<Booking>> = Bookings.getBookings().asLiveData()

    private val _bookingToView = MutableLiveData<Booking>()
    val bookingToView: LiveData<Booking> get() = _bookingToView

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