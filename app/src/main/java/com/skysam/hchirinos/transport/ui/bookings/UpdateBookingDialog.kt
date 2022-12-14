package com.skysam.hchirinos.transport.ui.bookings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.skysam.hchirinos.transport.R
import com.skysam.hchirinos.transport.common.Classes
import com.skysam.hchirinos.transport.dataClasses.Booking
import com.skysam.hchirinos.transport.dataClasses.Bus
import com.skysam.hchirinos.transport.dataClasses.Payment
import com.skysam.hchirinos.transport.databinding.DialogEditBookingBinding
import com.skysam.hchirinos.transport.ui.common.ExitDialog
import com.skysam.hchirinos.transport.ui.common.OnClick
import com.skysam.hchirinos.transport.ui.payment.PaymentAdapter
import java.text.DateFormat
import java.util.*

/**
 * Created by Hector Chirinos on 02/12/2022.
 */

class UpdateBookingDialog: DialogFragment(), OnClick,
    com.skysam.hchirinos.transport.ui.payment.OnClick {
    private var _binding: DialogEditBookingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BookingViewModel by activityViewModels()
    private lateinit var dateSelected: Date
    private lateinit var booking: Booking
    private lateinit var adapterItems: PaymentAdapter
    private val payments = mutableListOf<Payment>()
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
        _binding = DialogEditBookingBinding.inflate(inflater, container, false)
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

        binding.etQuantity.doAfterTextChanged {
            binding.tfQuantity.error = null
            if (binding.etQuantity.text.toString().isNotEmpty()) {
                quantity = binding.etQuantity.text.toString().toInt()
                calculateDebt()
            }
        }

        adapterItems = PaymentAdapter(payments, false, this)
        binding.rvPayments.apply {
            setHasFixedSize(true)
            adapter = adapterItems
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        }

        binding.etName.doAfterTextChanged { binding.tfName.error = null }
        binding.etQuantity.doAfterTextChanged { binding.tfQuantity.error = null }

        binding.etDate.setOnClickListener { selecDate() }
        binding.btnExit.setOnClickListener { getOut() }
        binding.btnSave.setOnClickListener { validateData() }

        loadViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadViewModel() {
        viewModel.bookingToView.observe(viewLifecycleOwner) {
            if (_binding != null) {
                booking = it
                quantity = booking.quantity
                binding.etName.setText(booking.name)
                dateSelected = booking.date
                binding.etDate.setText(DateFormat.getDateInstance().format(dateSelected))
                binding.etQuantity.setText(booking.quantity.toString())
                payments.clear()
                payments.addAll(booking.payments)
                adapterItems.notifyItemRangeInserted(0, booking.payments.size)

                if (payments.isEmpty()) {
                    binding.tvTitle.text = getString(R.string.text_no_payments)
                    binding.rvPayments.visibility = View.GONE
                    binding.tvPaid.visibility = View.GONE
                } else {
                    binding.tvTitle.text = getString(R.string.text_resume_payments)
                }
                calculatePaid()
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

    private fun calculatePaid() {
        totalPay = 0.0
        for (pay in payments) {
            totalPay += pay.amount
        }
        binding.tvPaid.text = getString(R.string.text_total_amount_paid, Classes.convertDoubleToString(totalPay))
    }

    private fun calculateDebt() {
        binding.tvDebt.text = getString(R.string.text_total_amount_debt,
            Classes.convertDoubleToString((priceSeat * quantity) - totalPay))
    }

    private fun selecDate() {
        val builder = MaterialDatePicker.Builder.datePicker()
        val calendar = Calendar.getInstance()
        val picker = builder.build()
        picker.addOnPositiveButtonClickListener { selection: Long? ->
            calendar.timeInMillis = selection!!
            val timeZone = TimeZone.getDefault()
            val offset = timeZone.getOffset(Date().time) * -1
            calendar.timeInMillis = calendar.timeInMillis + offset
            dateSelected = calendar.time
            binding.etDate.setText(DateFormat.getDateInstance().format(dateSelected))
        }
        picker.show(requireActivity().supportFragmentManager, picker.toString())
    }

    private fun validateData() {
        binding.tfName.error = null
        binding.tfQuantity.error = null

        val name = binding.etName.text.toString()
        if (name.isEmpty()) {
            binding.tfName.error = getString(R.string.error_field_empty)
            binding.etName.requestFocus()
            return
        }
        val quantityS = binding.etQuantity.text.toString()
        if (quantityS.isEmpty()) {
            binding.tfQuantity.error = getString(R.string.error_field_empty)
            binding.etQuantity.requestFocus()
            return
        }
        if (quantityS.toInt() == 0) {
            binding.tfQuantity.error = getString(R.string.error_quantity_zero)
            binding.etQuantity.requestFocus()
            return
        }

        val booking = Booking(
            booking.id,
            name,
            quantityS.toInt(),
            dateSelected,
            payments
        )

        viewModel.updateBooking(booking)
        dismiss()
    }

    private fun getOut() {
        val exitDialog = ExitDialog(this)
        exitDialog.show(requireActivity().supportFragmentManager, tag)
    }

    override fun onClickExit() {
        dismiss()
    }

    override fun delete(payment: Payment) {
        adapterItems.notifyItemRemoved(payments.indexOf(payment))
        payments.remove(payment)
        calculatePaid()
        calculateDebt()
    }

}