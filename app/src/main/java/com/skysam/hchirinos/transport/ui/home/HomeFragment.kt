package com.skysam.hchirinos.transport.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.skysam.hchirinos.transport.R
import com.skysam.hchirinos.transport.common.Classes
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
    private var totalRefunds = 0.0

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
        binding.button.setOnClickListener {
            startActivity(Intent(requireContext(), NewBookingActivity::class.java))
            /*val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(
                    "https://play.google.com/store/apps/details?id=com.skysam.hchirinos.transport")
                setPackage("com.android.vending")
            }
            requireActivity().startActivity(intent)*/
        }

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
                totalRefunds = 0.0
                for (book in bookings) {
                    seatsReserved += book.quantity
                    for (payment in book.payments) {
                        totalPaid += payment.amount
                    }
                    for (refund in book.refunds) {
                        totalRefunds += refund.amount
                    }
                }
                showSeats()
                showPaids()
                showRefunds()
                showBookingsBalance()
            }
        }
        viewModel.bus.observe(viewLifecycleOwner) {
            if (_binding != null) {
                bus = it
                showSeats()
                showPaids()
                showRefunds()
                showBookingsBalance()
                viewModel.changePriceSeat(bus!!)
            }
        }
    }

    private fun showSeats() {
        if (bus != null) {
            binding.tvAvailables.text = if (bus!!.quantity != 0) (bus!!.quantity - seatsReserved).toString()
            else getString(R.string.text_not_define)
            binding.tvReserved.text = seatsReserved.toString()
        }
    }

    private fun showPaids() {
        if (bus != null) {
            binding.tvRemaining.text =  if (bus!!.price != 0.0) (bus!!.price + totalRefunds - totalPaid).toString()
            else getString(R.string.text_not_define)
            binding.tvCollected.text = (totalPaid - totalRefunds).toString()
        }
    }

    private fun showRefunds() {
        if (bus != null) {
            var total = 0.0
            for (booking in bookings) {
                val diff = Classes.getTotalBooking(booking.quantity) + Classes.totalRefunds(booking.refunds) -
                        Classes.totalPayments(booking.payments)
                if (diff < 0.0) total += diff
            }
            if (total != 0.0) {
                binding.tvRefund.visibility = View.VISIBLE
                binding.textView9.visibility = View.VISIBLE
                binding.tvRefund.text = (total * (-1)).toString()
            } else {
                binding.tvRefund.visibility = View.GONE
                binding.textView9.visibility = View.GONE
            }
        }
    }

    private fun showBookingsBalance() {
        if (bus != null) {
            var totalPaid = 0
            for (booking in bookings) {
                val diff = Classes.getTotalBooking(booking.quantity) -
                        Classes.totalPayments(booking.payments)
                if (diff <= 0) totalPaid += 1
            }
            binding.tvBookingsPaid.text = totalPaid.toString()
            binding.tvBookingsRemaning.text = (bookings.size - totalPaid).toString()
        }
    }
}