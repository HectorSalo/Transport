package com.skysam.hchirinos.transport.ui.passengers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skysam.hchirinos.transport.dataClasses.Booking
import com.skysam.hchirinos.transport.dataClasses.Passenger
import com.skysam.hchirinos.transport.dataClasses.PassengerRow
import com.skysam.hchirinos.transport.databinding.BottomsheetPassengersBinding
import com.skysam.hchirinos.transport.ui.bookings.BookingViewModel

/**
 * Created by Hector Chirinos in the home office on 27 ene. 2026
 */
class PassengersBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomsheetPassengersBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BookingViewModel by activityViewModels()
    private lateinit var passengersAdapter: PassengerEditAdapter

    private var booking: Booking? = null
    private var expectedQuantity: Int = 0
    private var initialized = false

    override fun getTheme(): Int {
        // Overlay M3 del material (ya existe en la lib)
        return com.google.android.material.R.style.ThemeOverlay_Material3_BottomSheetDialog
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

        binding.rvPassengers.adapter = passengersAdapter

        binding.rvPassengers.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPassengers.adapter = passengersAdapter

        binding.btnSave.setOnClickListener {
            save()
        }

        // Observa el booking seleccionado
        viewModel.bookingToView.observe(viewLifecycleOwner) { b ->
            if (!initialized) {
                initialized = true
                booking = b
                expectedQuantity = b.quantity
                binding.tvTitle.text = "Pasajeros"

                val rows = buildRows(expectedQuantity, b.passengers)
                passengersAdapter.setRows(rows)
                passengersAdapter.recomputeExtraFlags(expectedQuantity)
            }
        }
    }

    private fun buildRows(quantity: Int, passengers: List<Passenger>): List<PassengerRow> {
        // muestra slots hasta max(quantity, passengers.size)
        val size = maxOf(quantity, passengers.size)
        val list = MutableList(size) { index ->
            val p = passengers.getOrNull(index) ?: Passenger()
            PassengerRow(passenger = p, isExtra = index >= quantity)
        }
        return list
    }

    private fun updateHeader() {
        val qty = expectedQuantity.coerceAtLeast(0)
        val rows = passengersAdapter.getRows()

        val filled = rows
            .take(qty)
            .count { it.passenger.fullName.isNotBlank() && it.passenger.documentNumber.isNotBlank() }

        val missing = (qty - filled).coerceAtLeast(0)
        val extras = (rows.size - qty).coerceAtLeast(0)

        val base = "Cédulas completas $filled/$qty · Faltan $missing"
        binding.tvSubtitle.text = if (extras > 0) "$base · Sobran $extras" else base
    }

    private fun save() {
        val b = booking ?: return

        // Sanitizar: guarda solo filas donde haya algo escrito.
        val sanitized = passengersAdapter.getRows()
            .map { it.passenger }
            .filter { it.fullName.isNotBlank() || it.documentNumber.isNotBlank() }
            .map {
                // Normaliza tipo doc
                it.apply {
                    documentType = documentType.ifBlank { "V" }
                }
            }

        viewModel.savePassengers(b.id, sanitized)
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
