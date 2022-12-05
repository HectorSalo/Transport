package com.skysam.hchirinos.transport.ui.bookings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.skysam.hchirinos.transport.R
import com.skysam.hchirinos.transport.dataClasses.Booking
import com.skysam.hchirinos.transport.dataClasses.Bus
import com.skysam.hchirinos.transport.databinding.FragmentBookingBinding
import com.skysam.hchirinos.transport.ui.common.WrapLayoutManager
import com.skysam.hchirinos.transport.ui.payment.AddPaymentDialog
import com.skysam.hchirinos.transport.ui.payment.ViewDetailsDialog

class BookingFragment : Fragment(), OnClick {

    private var _binding: FragmentBookingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BookingViewModel by activityViewModels()
    private lateinit var bookingAdapter: BookingAdapter
    private val bookings = mutableListOf<Booking>()
    private lateinit var wrapLayoutManager: WrapLayoutManager

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
        wrapLayoutManager = WrapLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.rvBookings.apply {
            setHasFixedSize(true)
            adapter = bookingAdapter
            layoutManager = wrapLayoutManager
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
                    val listTemp = mutableListOf<Booking>()
                    for (item in bookings) {
                        listTemp.add(item)
                    }
                    bookings.clear()
                    bookings.addAll(it)

                    if (listTemp.size == bookings.size) updateItem(listTemp)
                    if (listTemp.size < bookings.size) addItem(listTemp)
                    if (listTemp.size > bookings.size) deleteItem(listTemp)

                    binding.tvListEmpty.visibility = View.GONE
                    binding.rvBookings.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun deleteItem(listTemp: MutableList<Booking>) {
        for (itemTemp in listTemp) {
            if (!bookings.contains(itemTemp)) {
                bookingAdapter.notifyItemRemoved(listTemp.indexOf(itemTemp))
            }
        }
    }

    private fun addItem(listTemp: MutableList<Booking>) {
        if (listTemp.isEmpty()) {
            bookingAdapter.notifyItemRangeInserted(0, bookings.size)
            return
        }
        for (itemBook in bookings) {
            if (!listTemp.contains(itemBook)) {
                bookingAdapter.notifyItemInserted(bookings.indexOf(itemBook))
            }
        }
    }

    private fun updateItem(listTemp: MutableList<Booking>) {
        for (itemTemp in listTemp) {
            for (itemBook in bookings){
                if (itemTemp.id == itemBook.id && itemTemp != itemBook) {
                    bookingAdapter.notifyItemChanged(bookings.indexOf(itemBook))
                    break
                }
            }
        }
    }

    override fun view(booking: Booking) {
        viewModel.viewBooking(booking)
        val viewDetailsDialog = ViewDetailsDialog()
        viewDetailsDialog.show(requireActivity().supportFragmentManager, tag)
    }

    override fun edit(booking: Booking) {
        viewModel.viewBooking(booking)
        val updateBookingDialog = UpdateBookingDialog()
        updateBookingDialog.show(requireActivity().supportFragmentManager, tag)
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
        viewModel.viewBooking(booking)
        val addPaymentDialog = AddPaymentDialog()
        addPaymentDialog.show(requireActivity().supportFragmentManager, tag)
    }
}