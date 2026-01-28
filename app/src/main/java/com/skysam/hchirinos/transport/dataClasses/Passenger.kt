package com.skysam.hchirinos.transport.dataClasses

/**
 * Created by Hector Chirinos in the home office on 27 ene. 2026
 */
data class Passenger(
    var fullName: String = "",
    var documentType: String = "V",
    var documentNumber: String = ""
)

data class PassengerRow(
    var passenger: Passenger,
    var isExtra: Boolean = false
)
