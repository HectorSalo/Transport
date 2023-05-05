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
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import com.skysam.hchirinos.transport.R
import com.skysam.hchirinos.transport.common.Classes
import com.skysam.hchirinos.transport.dataClasses.Booking
import com.skysam.hchirinos.transport.dataClasses.Payment
import com.skysam.hchirinos.transport.dataClasses.Refund
import com.skysam.hchirinos.transport.databinding.DialogEditBookingBinding
import com.skysam.hchirinos.transport.ui.common.ExitDialog
import com.skysam.hchirinos.transport.ui.common.OnClick
import com.skysam.hchirinos.transport.ui.common.WrapLayoutManager
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
    private lateinit var wrapLayoutManager: WrapLayoutManager
    private val payments = mutableListOf<Payment>()
    private val paymentsToSee = mutableListOf<Payment>()
    private val refunds = mutableListOf<Refund>()
    private var seePayments = true

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
                booking.quantity = binding.etQuantity.text.toString().toInt()
                calculate()
            }
        }

        adapterItems = PaymentAdapter(paymentsToSee, false, this)
        wrapLayoutManager = WrapLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.rvPayments.apply {
            setHasFixedSize(true)
            adapter = adapterItems
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
            layoutManager = wrapLayoutManager
        }

        binding.etName.doAfterTextChanged { binding.tfName.error = null }
        binding.etQuantity.doAfterTextChanged { binding.tfQuantity.error = null }

        binding.radioGroup2.setOnCheckedChangeListener { _, id ->
            seePayments = id == R.id.rb_payments
            changeAdapter()
        }

        binding.rgAssembly.setOnCheckedChangeListener {_, id ->
            booking.days = when(id) {
                R.id.rb_1 -> 1
                R.id.rb_2 -> 2
                R.id.rb_3 -> 3
                else -> 1
            }
            calculate()
        }

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
                binding.etName.setText(booking.name)
                dateSelected = booking.date
                binding.etDate.setText(DateFormat.getDateInstance().format(dateSelected))
                binding.etQuantity.setText(booking.quantity.toString())

                when(booking.days) {
                    1 -> binding.rb1.isChecked = true
                    2 -> binding.rb2.isChecked = true
                    3 -> binding.rb3.isChecked = true
                }

                payments.clear()
                payments.addAll(booking.payments)
                refunds.clear()
                refunds.addAll(booking.refunds)
                changeAdapter()

                if (payments.isEmpty()) {
                    binding.rvPayments.visibility = View.GONE
                    binding.tvPaid.visibility = View.GONE
                } else {
                    binding.tvTitle.visibility = View.GONE
                }
                calculatePaid()
                calculate()
            }
        }

        viewModel.assemblyActive.observe(viewLifecycleOwner) {
            if (_binding != null) {
                if (it) {
                    binding.textView1.visibility = View.VISIBLE
                    binding.rgAssembly.visibility = View.VISIBLE
                } else {
                    binding.textView1.visibility = View.GONE
                    binding.rgAssembly.visibility = View.GONE
                }
            }
        }
    }

    private fun calculatePaid() {
        binding.tvPaid.text = getString(R.string.text_total_amount_paid,
            Classes.convertDoubleToString(Classes.totalPayments(payments)))
    }

    private fun calculate() {
        val diff = Classes.getTotalBooking(booking) + Classes.totalRefunds(refunds) -
                Classes.totalPayments(payments)
        if (diff == 0.0) binding.tvDebt.visibility = View.GONE
        if (diff > 0.0) binding.tvDebt.text = getString(R.string.text_total_amount_debt,
            Classes.convertDoubleToString(diff))
        if (diff < 0.0) binding.tvDebt.text = getString(R.string.text_total_amount_refund_pending,
            Classes.convertDoubleToString(diff * (-1)))
    }

    private fun changeAdapter() {
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

        val bookingUpdated = Booking(
            booking.id,
            name,
            quantityS.toInt(),
            dateSelected,
            payments,
            refunds,
            booking.days
        )

        viewModel.updateBooking(bookingUpdated)
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
        adapterItems.notifyItemRemoved(paymentsToSee.indexOf(payment))
        paymentsToSee.remove(payment)
        if (seePayments) {
            payments.remove(payment)
        } else {
            val newRef = Refund(
                payment.payer,
                payment.receiver,
                payment.date,
                payment.amount
            )
            refunds.remove(newRef)
        }
        calculatePaid()
        calculate()
    }

}