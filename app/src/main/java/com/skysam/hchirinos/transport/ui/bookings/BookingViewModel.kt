package com.skysam.hchirinos.transport.ui.bookings

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.skysam.hchirinos.transport.dataClasses.Booking
import com.skysam.hchirinos.transport.repositories.Bookings

class BookingViewModel : ViewModel() {
    val bookings: LiveData<MutableList<Booking>> = Bookings.getBookings().asLiveData()

    fun addBooking(booking: Booking) {
        Bookings.addBooking(booking)
    }

    fun deleteBooking(booking: Booking) {
        Bookings.deleteBooking(booking.id)
    }
}