package com.skysam.hchirinos.transport.ui.bookings.newBooking

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.skysam.hchirinos.transport.databinding.ActivityNewBookingBinding

class NewBookingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewBookingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}