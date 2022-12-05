package com.skysam.hchirinos.transport.ui.bookings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.skysam.hchirinos.transport.dataClasses.Booking
import com.skysam.hchirinos.transport.dataClasses.Bus
import com.skysam.hchirinos.transport.dataClasses.Payment
import com.skysam.hchirinos.transport.repositories.Bookings
import com.skysam.hchirinos.transport.repositories.Busses

class BookingViewModel : ViewModel() {
    val bookings: LiveData<MutableList<Booking>> = Bookings.getBookings().asLiveData()
    val bus: LiveData<Bus> = Busses.getBus().asLiveData()

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

    fun updateBooking(booking: Booking) {
        Bookings.updateBooking(booking)
    }
}