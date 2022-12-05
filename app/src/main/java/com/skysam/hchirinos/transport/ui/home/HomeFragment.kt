package com.skysam.hchirinos.transport.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.skysam.hchirinos.transport.dataClasses.Booking
import com.skysam.hchirinos.transport.dataClasses.Bus
import com.skysam.hchirinos.transport.databinding.FragmentHomeBinding
import com.skysam.hchirinos.transport.ui.bookings.newBooking.NewBookingActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by activityViewModels()
    private val bookings = mutableListOf<Booking>()
    private var bus: Bus? = null
    private var seatsReserved = 0
    private var totalPaid = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.button.setOnClickListener { startActivity(Intent(requireContext(), NewBookingActivity::class.java)) }

        loadViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadViewModel() {
        viewModel.bookings.observe(viewLifecycleOwner) {
            if (_binding != null) {
                bookings.clear()
                bookings.addAll(it)
                seatsReserved = 0
                totalPaid = 0.0
                for (book in bookings) {
                    seatsReserved += book.quantity
                    for (payment in book.payments) {
                        totalPaid += payment.amount
                    }
                }
                showSeats()
                showPaids()
            }
        }
        viewModel.bus.observe(viewLifecycleOwner) {
            if (_binding != null) {
                bus = it
                showSeats()
                showPaids()
            }
        }
    }

    private fun showSeats() {
        if (bus != null) {
            binding.tvAvailables.text = (bus!!.quantity - seatsReserved).toString()
            binding.tvReserved.text = seatsReserved.toString()
        }
    }

    private fun showPaids() {
        if (bus != null) {
            binding.tvRemaining.text = (bus!!.price - totalPaid).toString()
            binding.tvCollected.text = totalPaid.toString()
        }
    }
}