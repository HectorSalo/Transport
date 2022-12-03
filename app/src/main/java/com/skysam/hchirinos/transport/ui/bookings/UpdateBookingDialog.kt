package com.skysam.hchirinos.transport.ui.bookings

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.skysam.hchirinos.transport.R
import com.skysam.hchirinos.transport.dataClasses.Booking
import com.skysam.hchirinos.transport.dataClasses.Payment
import com.skysam.hchirinos.transport.databinding.DialogEditBookingBinding
import com.skysam.hchirinos.transport.databinding.FragmentFirstNewBinding
import com.skysam.hchirinos.transport.ui.common.ExitDialog
import com.skysam.hchirinos.transport.ui.common.OnClick
import java.text.DateFormat
import java.util.*

/**
 * Created by Hector Chirinos on 02/12/2022.
 */

class UpdateBookingDialog: DialogFragment(), OnClick {
    private var _binding: DialogEditBookingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BookingViewModel by activityViewModels()
    private lateinit var dateSelected: Date
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

        dateSelected = Date()
        binding.etDate.setText(DateFormat.getDateInstance().format(dateSelected))

        binding.etName.doAfterTextChanged { binding.tfName.error = null }
        binding.etQuantity.doAfterTextChanged { binding.tfQuantity.error = null }

        binding.etDate.setOnClickListener { selecDate() }
        /*binding.btnExit.setOnClickListener { getOut() }
        binding.btnSave.setOnClickListener { validateData() }*/
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

        val payments = mutableListOf<Payment>()

        val booking = Booking(
            "",
            name,
            quantityS.toInt(),
            dateSelected,
            payments
        )

        viewModel.addBooking(booking)
        requireActivity().finish()
    }

    private fun getOut() {
        val exitDialog = ExitDialog(this)
        exitDialog.show(requireActivity().supportFragmentManager, tag)
    }

    override fun onClickExit() {
        dismiss()
    }

}