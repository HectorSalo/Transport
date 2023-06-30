package com.skysam.hchirinos.transport.ui.bookings

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import com.skysam.hchirinos.transport.R
import com.skysam.hchirinos.transport.dataClasses.Booking
import com.skysam.hchirinos.transport.databinding.FragmentBookingBinding
import com.skysam.hchirinos.transport.ui.common.WrapLayoutManager
import com.skysam.hchirinos.transport.ui.payment.AddPaymentDialog
import com.skysam.hchirinos.transport.ui.refund.AddRefundDialog

class BookingFragment : Fragment(), OnClick, MenuProvider, SearchView.OnQueryTextListener {

    private var _binding: FragmentBookingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BookingViewModel by activityViewModels()
    private lateinit var bookingAdapter: BookingAdapter
    private var bookings = listOf<Booking>()
    private lateinit var wrapLayoutManager: WrapLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingBinding.inflate(inflater, container, false)
        binding.topAppBar.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bookingAdapter = BookingAdapter(this)
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
                if (it.isNotEmpty()) {
                    bookings = it
                    bookingAdapter.updateList(bookings)
                    binding.tvListEmpty.visibility = View.GONE
                    binding.rvBookings.visibility = View.VISIBLE
                } else {
                    binding.tvListEmpty.visibility = View.VISIBLE
                    binding.rvBookings.visibility = View.GONE
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

    override fun addRefund(booking: Booking) {
        viewModel.viewBooking(booking)
        val addRefundDialog = AddRefundDialog()
        addRefundDialog.show(requireActivity().supportFragmentManager, tag)
    }

    override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
        val itemSearch = menu.findItem(R.id.app_bar_search)
        val search = itemSearch.actionView as SearchView
        search.isSubmitButtonEnabled = true
        search.setOnQueryTextListener(this)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when(menuItem.itemId) {
            R.id.app_bar_search -> true
            else -> false
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (bookings.isNotEmpty()) {
            val userInput: String = newText!!.lowercase()

            val list = mutableListOf<Booking>()

            if (userInput.isNotEmpty()) {
                for (booking in bookings) {
                    if (booking.name.lowercase().contains(userInput)) {
                        list.add(booking)
                    }
                }
                if (list.isEmpty()) {
                    binding.lottieAnimationView.visibility = View.VISIBLE
                    binding.lottieAnimationView.playAnimation()
                } else {
                    binding.lottieAnimationView.visibility = View.GONE
                }
                bookingAdapter.updateList(list)
            } else {
                bookingAdapter.updateList(bookings)
                binding.lottieAnimationView.visibility = View.GONE
            }
        }
        return true
    }
}