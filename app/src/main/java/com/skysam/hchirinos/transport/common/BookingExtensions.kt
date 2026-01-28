package com.skysam.hchirinos.transport.common

import com.skysam.hchirinos.transport.dataClasses.Booking
import com.skysam.hchirinos.transport.dataClasses.Passenger

/**
 * Created by Hector Chirinos in the home office on 27 ene. 2026
 */

fun Booking.passengersFilledCount(): Int {
    return passengers.count { it.fullName.isNotBlank() && it.documentNumber.isNotBlank() }
}

fun Booking.passengersMissingCount(): Int {
    // faltan por completar “hasta quantity”, pero sin obligar a tener placeholders
    val filled = passengersFilledCount().coerceAtMost(quantity)
    return (quantity - filled).coerceAtLeast(0)
}

fun Booking.hasExtraPassengers(): Boolean {
    return passengers.size > quantity
}

fun Booking.extraPassengersCount(): Int {
    return (passengers.size - quantity).coerceAtLeast(0)
}

fun Booking.completedPassengers(): List<Passenger> =
    passengers.filter { it.fullName.isNotBlank() && it.documentNumber.isNotBlank() }


