package com.skysam.hchirinos.transport.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.skysam.hchirinos.transport.common.Preferences
import com.skysam.hchirinos.transport.dataClasses.Booking
import com.skysam.hchirinos.transport.dataClasses.Bus
import com.skysam.hchirinos.transport.dataClasses.InfoApp
import com.skysam.hchirinos.transport.repositories.Bookings
import com.skysam.hchirinos.transport.repositories.Busses

class HomeViewModel : ViewModel() {
    val infoApp: LiveData<InfoApp> = com.skysam.hchirinos.transport.repositories.InfoApp.getInfoApp().asLiveData()
    val bookings: LiveData<MutableList<Booking>> = Bookings.getBookings().asLiveData()
    val bus: LiveData<Bus> = Busses.getBus().asLiveData()

    fun changePriceSeat(bus: Bus) {
        Preferences.changePriceSeat(bus)
    }
}