package com.skysam.hchirinos.transport.dataClasses

import java.util.*

/**
 * Created by Hector Chirinos on 14/01/2023.
 */

data class Refund(
 var payer: String,
 var receiver: String,
 var date: Date,
 var amount: Double
)
