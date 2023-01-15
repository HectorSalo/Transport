package com.skysam.hchirinos.transport.ui.bookings

import com.skysam.hchirinos.transport.dataClasses.Booking

/**
 * Created by Hector Chirinos on 02/12/2022.
 */

interface OnClick {
    fun view(booking: Booking)
    fun edit(booking: Booking)
    fun delete(booking: Booking)
    fun addPayment(booking: Booking)
    fun addRefund(booking: Booking)
}