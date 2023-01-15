package com.skysam.hchirinos.transport.dataClasses

import java.util.Date

/**
 * Created by Hector Chirinos on 01/12/2022.
 */

data class Booking(
    val id: String,
    var name: String,
    var quantity: Int = 1,
    var date: Date,
    var payments: MutableList<Payment> = mutableListOf(),
    var refunds: MutableList<Refund> = mutableListOf()
)
