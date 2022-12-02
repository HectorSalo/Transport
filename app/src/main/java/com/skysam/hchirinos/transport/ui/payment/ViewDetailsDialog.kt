package com.skysam.hchirinos.transport.ui.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.skysam.hchirinos.transport.R
import com.skysam.hchirinos.transport.common.Classes
import com.skysam.hchirinos.transport.dataClasses.Booking
import com.skysam.hchirinos.transport.dataClasses.Payment
import com.skysam.hchirinos.transport.databinding.DialogViewDetailsBookingBinding
import com.skysam.hchirinos.transport.ui.bookings.BookingViewModel

/**
 * Created by Hector Chirinos on 02/12/2022.
 */

class ViewDetailsDialog: DialogFragment() {
    private var _binding: DialogViewDetailsBookingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BookingViewModel by activityViewModels()
    private lateinit var adapterItems: PaymentAdapter
    private val payments = mutableListOf<Payment>()
    private lateinit var booking: Booking

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
        adapterItems = PaymentAdapter(payments, true)
        binding.rvPayments.apply {
            setHasFixedSize(true)
            adapter = adapterItems
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
                payments.clear()
                payments.addAll(booking.payments)
                var totalPay = 0.0
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
                    binding.tvDebt.text = getString(R.string.text_total_amount_debt, Classes.convertDoubleToString(totalPay))
                } else {
                    binding.tvTitle.text = getString(R.string.text_resume_payments)
                    binding.tvPaid.text = getString(R.string.text_total_amount_paid, Classes.convertDoubleToString(totalPay))
                    binding.tvDebt.text = getString(R.string.text_total_amount_debt, Classes.convertDoubleToString(totalPay))
                }
            }
        }
    }
}