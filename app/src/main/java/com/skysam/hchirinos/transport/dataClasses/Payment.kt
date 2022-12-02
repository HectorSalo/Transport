package com.skysam.hchirinos.transport.dataClasses

import java.util.Date

/**
 * Created by Hector Chirinos on 01/12/2022.
 */

data class Payment(
 var whoPaid: String,
 var whoReceived: String,
 var date: Date,
 var amount: Double
)
