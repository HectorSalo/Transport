package com.skysam.hchirinos.transport.ui.bookings.newBooking

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.skysam.hchirinos.transport.R
import com.skysam.hchirinos.transport.dataClasses.Booking
import com.skysam.hchirinos.transport.dataClasses.Payment
import com.skysam.hchirinos.transport.databinding.FragmentFirstNewBinding
import com.skysam.hchirinos.transport.ui.bookings.BookingViewModel
import com.skysam.hchirinos.transport.ui.common.ExitDialog
import com.skysam.hchirinos.transport.ui.common.OnClick
import java.text.DateFormat
import java.util.*


class FirstNewFragment : Fragment(), TextWatcher, OnClick {

    private var _binding: FragmentFirstNewBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BookingViewModel by activityViewModels()
    private lateinit var dateSelected: Date
    private var assambly = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstNewBinding.inflate(inflater, container, false)
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

        dateSelected = Date()
        binding.etDate.setText(DateFormat.getDateInstance().format(dateSelected))
        binding.etAmount.addTextChangedListener(this)

        binding.etName.doAfterTextChanged { binding.tfName.error = null }
        binding.etQuantity.doAfterTextChanged { binding.tfQuantity.error = null }
        binding.etPayer.doAfterTextChanged { binding.tfPayer.error = null }
        binding.etAmount.doAfterTextChanged { binding.tfAmount.error = null }

        binding.etDate.setOnClickListener { selecDate() }
        binding.btnExit.setOnClickListener { getOut() }
        binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) binding.constraint.visibility = View.VISIBLE
            else binding.constraint.visibility = View.GONE
        }

        binding.btnSave.setOnClickListener { validateData() }

        loadViewModels()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadViewModels() {
        viewModel.assemblyActive.observe(viewLifecycleOwner) {
            if (_binding != null) {
                assambly = it
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
        binding.tfPayer.error = null
        binding.tfAmount.error = null

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

        var days = 1
        if (assambly) {
            if (binding.rb1.isChecked) days = 1
            if (binding.rb2.isChecked) days = 2
            if (binding.rb3.isChecked) days = 3
        }

        val payments = mutableListOf<Payment>()

        if (binding.checkBox.isChecked) {
            if (!binding.rbCarlos.isChecked && !binding.rbHector.isChecked && !binding.rbAduin.isChecked) {
                Snackbar.make(binding.root, getString(R.string.error_receiver_null), Snackbar.LENGTH_LONG)
                .setAnchorView(binding.btnSave)
                .show()
                return
            }
            var receiver = ""
            if (binding.rbCarlos.isChecked) receiver = getString(R.string.text_carlos)
            if (binding.rbHector.isChecked) receiver = getString(R.string.text_hector)
            if (binding.rbAduin.isChecked) receiver = getString(R.string.text_aduin)

            val payer = binding.etPayer.text.toString()
            if (payer.isEmpty()) {
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

            val payment = Payment(
                payer,
                receiver,
                dateSelected,
                amountS.toDouble()
            )

            payments.add(payment)
        }

        val booking = Booking(
            "",
            name,
            quantityS.toInt(),
            dateSelected,
            payments,
            days = days
        )

        viewModel.addBooking(booking)
        requireActivity().finish()
    }

    private fun getOut() {
        val exitDialog = ExitDialog(this)
        exitDialog.show(requireActivity().supportFragmentManager, tag)
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

    override fun onClickExit() {
        requireActivity().finish()
    }
}