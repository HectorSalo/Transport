package com.skysam.hchirinos.transport.dataClasses

import java.util.Date

data class Event(
    val id: String,
    val title: String,
    val image: String,
    val date: Date
)
