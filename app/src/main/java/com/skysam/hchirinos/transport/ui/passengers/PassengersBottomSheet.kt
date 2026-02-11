package com.skysam.hchirinos.transport.ui.passengers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.skysam.hchirinos.transport.dataClasses.Booking
import com.skysam.hchirinos.transport.dataClasses.Passenger
import com.skysam.hchirinos.transport.dataClasses.PassengerRow
import com.skysam.hchirinos.transport.databinding.BottomsheetPassengersBinding
import com.skysam.hchirinos.transport.ui.bookings.BookingViewModel

/**
 * Created by Hector Chirinos in the home office on 27 ene. 2026
 */
class PassengersBottomSheet : DialogFragment() {


    private var _binding: BottomsheetPassengersBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BookingViewModel by activityViewModels()
    private lateinit var passengersAdapter: PassengerEditAdapter

    private var booking: Booking? = null
    private var expectedQuantity: Int = 0
    private var initialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL,
            com.google.android.material.R.style.ShapeAppearanceOverlay_MaterialComponents_MaterialCalendar_Window_Fullscreen
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BottomsheetPassengersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        passengersAdapter = PassengerEditAdapter(
            onChanged = { updateHeader() },
            onDeleteExtra = { pos -> passengersAdapter.removeAt(pos) }
        )

        binding.rvPassengers.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPassengers.adapter = passengersAdapter

        binding.btnSave.setOnClickListener { save() }

        viewModel.bookingToView.observe(viewLifecycleOwner) { b ->
            if (!initialized) {
                initialized = true
                booking = b
                expectedQuantity = b.quantity
                binding.tvTitle.text = "Pasajeros"

                val rows = buildRows(expectedQuantity, b.passengers)
                passengersAdapter.setRows(rows)
                passengersAdapter.recomputeExtraFlags(expectedQuantity)
                updateHeader()
            }
        }
    }

    private fun buildRows(quantity: Int, passengers: List<Passenger>): List<PassengerRow> {
        val size = maxOf(quantity, passengers.size)
        return MutableList(size) { index ->
            val p = passengers.getOrNull(index) ?: Passenger()
            PassengerRow(passenger = p, isExtra = index >= quantity)
        }
    }

    private fun updateHeader() {
        val qty = expectedQuantity.coerceAtLeast(0)
        val rows = passengersAdapter.getRows()

        val filled = rows.take(qty)
            .count { it.passenger.fullName.isNotBlank() && it.passenger.documentNumber.isNotBlank() }

        val missing = (qty - filled).coerceAtLeast(0)
        val extras = (rows.size - qty).coerceAtLeast(0)

        val base = "Cédulas completas $filled/$qty · Faltan $missing"
        binding.tvSubtitle.text = if (extras > 0) "$base · Sobran $extras" else base
    }

    private fun save() {
        val b = booking ?: return

        val sanitized = passengersAdapter.getRows()
            .map { it.passenger }
            .filter { it.fullName.isNotBlank() || it.documentNumber.isNotBlank() }
            .map { it.apply { documentType = documentType.ifBlank { "V" } } }

        viewModel.savePassengers(b.id, sanitized)
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
