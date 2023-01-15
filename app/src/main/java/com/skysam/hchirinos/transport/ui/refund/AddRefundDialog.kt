package com.skysam.hchirinos.transport.ui.refund

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.skysam.hchirinos.transport.R
import com.skysam.hchirinos.transport.dataClasses.Booking
import com.skysam.hchirinos.transport.dataClasses.Refund
import com.skysam.hchirinos.transport.databinding.DialogAddPaymentBinding
import com.skysam.hchirinos.transport.ui.bookings.BookingViewModel
import java.text.DateFormat
import java.util.*

/**
 * Created by Hector Chirinos on 14/01/2023.
 */

class AddRefundDialog: DialogFragment(), TextWatcher {
    private var _binding: DialogAddPaymentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BookingViewModel by activityViewModels()
    private lateinit var buttonPositive: Button
    private lateinit var buttonNegative: Button
    private lateinit var booking: Booking
    private lateinit var dateSelected: Date

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddPaymentBinding.inflate(layoutInflater)

        viewModel.bookingToView.observe(this.requireActivity()) {
            if (_binding != null) {
                booking = it
            }
        }

        dateSelected = Date()
        binding.etDate.setText(DateFormat.getDateInstance().format(dateSelected))

        binding.etAmount.addTextChangedListener(this)
        binding.etDate.setOnClickListener { selecDate() }

        binding.etPayer.doAfterTextChanged { binding.tfPayer.error = null }
        binding.etAmount.doAfterTextChanged { binding.tfAmount.error = null }

        binding.textView.text = getString(R.string.text_payer)
        binding.tfPayer.hint = getString(R.string.text_receiver_paid)

        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.text_add_refund))
            .setView(binding.root)
            .setPositiveButton(R.string.text_save, null)
            .setNegativeButton(R.string.text_cancel, null)

        val dialog = builder.create()
        dialog.show()

        buttonNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        buttonNegative.setOnClickListener { dialog.dismiss() }
        buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        buttonPositive.setOnClickListener { validateData() }

        return dialog

    }

    private fun validateData() {
        binding.tfPayer.error = null
        binding.tfAmount.error = null

        if (!binding.rbCarlos.isChecked && !binding.rbHector.isChecked && !binding.rbJesus.isChecked) {
            Toast.makeText(requireContext(), getString(R.string.error_receiver_null), Toast.LENGTH_SHORT)
                .show()
            return
        }
        var payer = ""
        if (binding.rbCarlos.isChecked) payer = getString(R.string.text_carlos)
        if (binding.rbHector.isChecked) payer = getString(R.string.text_hector)
        if (binding.rbJesus.isChecked) payer = getString(R.string.text_jesus)

        val receiver = binding.etPayer.text.toString()
        if (receiver.isEmpty()) {
            binding.tfPayer.error = getString(R.string.error_field_empty)
            binding.etPayer.requestFocus()
            return
        }
        var amountS = binding.etAmount.text.toString()
        if (amountS == "0,00") {
            binding.tfAmount.error = getString(R.string.error_amount_zero)
            binding.etAmount.requestFocus()
            return
        }
        amountS = amountS.replace(".", "").replace(",", ".")

        val payment = Refund(
            payer,
            receiver,
            dateSelected,
            amountS.toDouble()
        )

        viewModel.addRefund(booking, payment)
        dismiss()
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

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun afterTextChanged(s: Editable?) {
        var cadena = s.toString()
        cadena = cadena.replace(",", "").replace(".", "")
        val cantidad: Double = cadena.toDouble() / 100
        cadena = String.format(Locale.GERMANY, "%,.2f", cantidad)

        if (s.toString() == binding.etAmount.text.toString()) {
            binding.etAmount.removeTextChangedListener(this)
            binding.etAmount.setText(cadena)
            binding.etAmount.setSelection(cadena.length)
            binding.etAmount.addTextChangedListener(this)
        }
    }
}