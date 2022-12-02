package com.skysam.hchirinos.transport.ui.bookings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.skysam.hchirinos.transport.R
import com.skysam.hchirinos.transport.dataClasses.Booking
import com.skysam.hchirinos.transport.databinding.FragmentBookingBinding

class BookingFragment : Fragment(), OnClick {

    private var _binding: FragmentBookingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BookingViewModel by activityViewModels()
    private lateinit var bookingAdapter: BookingAdapter
    private val bookings = mutableListOf<Booking>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bookingAdapter = BookingAdapter(bookings, this)
        binding.rvBookings.apply {
            setHasFixedSize(true)
            adapter = bookingAdapter
        }

        loadViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadViewModel() {
        viewModel.bookings.observe(viewLifecycleOwner) {
            if (_binding != null) {
                binding.progressBar.visibility = View.GONE
                if (it.isEmpty()) {
                    binding.tvListEmpty.visibility = View.VISIBLE
                    binding.rvBookings.visibility = View.GONE
                } else {
                    bookings.clear()
                    bookings.addAll(it)
                    bookingAdapter.notifyItemRangeInserted(0, bookings.size)
                    binding.tvListEmpty.visibility = View.GONE
                    binding.rvBookings.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun view(booking: Booking) {
        TODO("Not yet implemented")
    }

    override fun edit(booking: Booking) {
        TODO("Not yet implemented")
    }

    override fun delete(booking: Booking) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_confirmation_dialog))
            .setMessage(getString(R.string.msg_delete_dialog))
            .setPositiveButton(R.string.text_delete) { _, _ ->
                viewModel.deleteBooking(booking)
            }
            .setNegativeButton(R.string.text_cancel, null)

        val dialog = builder.create()
        dialog.show()
    }

    override fun addPayment(booking: Booking) {
        TODO("Not yet implemented")
    }
}