package com.skysam.hchirinos.transport.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.skysam.hchirinos.transport.dataClasses.Booking
import com.skysam.hchirinos.transport.dataClasses.Bus
import com.skysam.hchirinos.transport.repositories.Bookings
import com.skysam.hchirinos.transport.repositories.Busses

class HomeViewModel : ViewModel() {
    val bookings: LiveData<MutableList<Booking>> = Bookings.getBookings().asLiveData()
    val bus: LiveData<Bus> = Busses.getBus().asLiveData()
}