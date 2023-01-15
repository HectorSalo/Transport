package com.skysam.hchirinos.transport.ui.bookings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skysam.hchirinos.transport.R
import com.skysam.hchirinos.transport.common.Classes
import com.skysam.hchirinos.transport.dataClasses.Booking
import com.skysam.hchirinos.transport.dataClasses.Payment
import com.skysam.hchirinos.transport.dataClasses.Refund
import com.skysam.hchirinos.transport.databinding.DialogViewDetailsBookingBinding
import com.skysam.hchirinos.transport.ui.common.WrapLayoutManager
import com.skysam.hchirinos.transport.ui.payment.OnClick
import com.skysam.hchirinos.transport.ui.payment.PaymentAdapter

/**
 * Created by Hector Chirinos on 02/12/2022.
 */

class ViewDetailsDialog: DialogFragment(), OnClick {
    private var _binding: DialogViewDetailsBookingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BookingViewModel by activityViewModels()
    private lateinit var adapterItems: PaymentAdapter
    private lateinit var wrapLayoutManager: WrapLayoutManager
    private val payments = mutableListOf<Payment>()
    private val paymentsToSee = mutableListOf<Payment>()
    private val refunds = mutableListOf<Refund>()
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
        adapterItems = PaymentAdapter(paymentsToSee, true, this)
        wrapLayoutManager = WrapLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.rvPayments.apply {
            setHasFixedSize(true)
            adapter = adapterItems
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
            layoutManager = wrapLayoutManager
        }

        binding.radioGroup2.setOnCheckedChangeListener { _, id->
            changeAdapter(id == R.id.rb_payments)
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
                refunds.clear()
                refunds.addAll(booking.refunds)
                changeAdapter(true)

                binding.tvName.text = booking.name
                binding.tvQuantity.text = getString(R.string.text_quantity_seats_item, booking.quantity.toString())
                binding.tvDate.text = Classes.convertDateToString(booking.date)

                if (payments.isEmpty()) {
                    binding.rvPayments.visibility = View.GONE
                    binding.tvPaid.visibility = View.GONE
                } else {
                    binding.tvPaid.text = getString(R.string.text_total_amount_paid,
                        Classes.convertDoubleToString(Classes.totalPayments(payments)))
                    binding.tvTitle.visibility = View.GONE
                }
                calculate()
            }
        }
    }

    private fun calculate() {
        val diff = Classes.getTotalBooking(booking.quantity) + Classes.totalRefunds(booking.refunds) -
                Classes.totalPayments(payments)
        if (diff == 0.0) binding.tvDebt.visibility = View.GONE
        if (diff > 0.0) binding.tvDebt.text = getString(R.string.text_total_amount_debt,
            Classes.convertDoubleToString(diff))
        if (diff < 0.0) binding.tvDebt.text = getString(R.string.text_total_amount_refund_pending,
            Classes.convertDoubleToString(diff * (-1)))
    }

    private fun changeAdapter(seePayments: Boolean) {
        if (seePayments) {
            adapterItems.notifyItemRangeRemoved(0, paymentsToSee.size)
            paymentsToSee.clear()
            paymentsToSee.addAll(payments)
            adapterItems.notifyItemRangeInserted(0, paymentsToSee.size)
            if (paymentsToSee.isEmpty()) {
                binding.tvTitle.text = getString(R.string.text_no_payments)
                binding.tvTitle.visibility = View.VISIBLE
            } else {
                binding.tvTitle.visibility = View.GONE
            }
        } else {
            adapterItems.notifyItemRangeRemoved(0, paymentsToSee.size)
            paymentsToSee.clear()
            for (ref in refunds) {
                val newPay = Payment(
                    ref.payer,
                    ref.receiver,
                    ref.date,
                    ref.amount
                )
                paymentsToSee.add(newPay)
            }
            adapterItems.notifyItemRangeInserted(0, paymentsToSee.size)
            if (paymentsToSee.isEmpty()) {
                binding.tvTitle.text = getString(R.string.text_no_refunds)
                binding.tvTitle.visibility = View.VISIBLE
            } else {
                binding.tvTitle.visibility = View.GONE
            }
        }
    }

    override fun delete(payment: Payment) {

    }
}