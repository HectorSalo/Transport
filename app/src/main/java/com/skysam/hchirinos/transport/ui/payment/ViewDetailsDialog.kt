package com.skysam.hchirinos.transport.ui.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.skysam.hchirinos.transport.R
import com.skysam.hchirinos.transport.common.Classes
import com.skysam.hchirinos.transport.dataClasses.Booking
import com.skysam.hchirinos.transport.dataClasses.Bus
import com.skysam.hchirinos.transport.dataClasses.Payment
import com.skysam.hchirinos.transport.databinding.DialogViewDetailsBookingBinding
import com.skysam.hchirinos.transport.ui.bookings.BookingViewModel

/**
 * Created by Hector Chirinos on 02/12/2022.
 */

class ViewDetailsDialog: DialogFragment(), OnClick {
    private var _binding: DialogViewDetailsBookingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BookingViewModel by activityViewModels()
    private lateinit var adapterItems: PaymentAdapter
    private val payments = mutableListOf<Payment>()
    private lateinit var booking: Booking
    private lateinit var bus: Bus
    private var totalPay = 0.0
    private var priceSeat = 0.0
    private var quantity = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL,
            com.google.android.material.R.style.ShapeAppearanceOverlay_MaterialComponents_MaterialCalendar_Window_Fullscreen
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogViewDetailsBookingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                getOut()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        adapterItems = PaymentAdapter(payments, true, this)
        binding.rvPayments.apply {
            setHasFixedSize(true)
            adapter = adapterItems
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        }

        loadViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getOut() {
        dismiss()
    }

    private fun loadViewModel() {
        viewModel.bookingToView.observe(viewLifecycleOwner) {
            if (_binding != null) {
                booking = it
                quantity = booking.quantity
                payments.clear()
                payments.addAll(booking.payments)
                for (pay in payments) {
                    totalPay += pay.amount
                }
                adapterItems.notifyItemRangeInserted(0, payments.size)

                binding.tvName.text = booking.name
                binding.tvQuantity.text = getString(R.string.text_quantity_seats_item, booking.quantity.toString())
                binding.tvDate.text = Classes.convertDateToString(booking.date)

                if (payments.isEmpty()) {
                    binding.tvTitle.text = getString(R.string.text_no_payments)
                    binding.rvPayments.visibility = View.GONE
                    binding.tvPaid.visibility = View.GONE
                } else {
                    binding.tvTitle.text = getString(R.string.text_resume_payments)
                    binding.tvPaid.text = getString(R.string.text_total_amount_paid, Classes.convertDoubleToString(totalPay))
                }
                calculateDebt()
            }
        }
        viewModel.bus.observe(viewLifecycleOwner) {
            if (_binding != null) {
                bus = it
                priceSeat = bus.price / bus.quantity
                calculateDebt()
            }
        }
    }

    private fun calculateDebt() {
        binding.tvDebt.text = getString(R.string.text_total_amount_debt,
            Classes.convertDoubleToString((priceSeat * quantity) - totalPay))
    }

    override fun delete(payment: Payment) {

    }
}