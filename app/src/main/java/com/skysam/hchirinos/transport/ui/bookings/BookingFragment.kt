package com.skysam.hchirinos.transport.ui.bookings

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.skysam.hchirinos.transport.R
import com.skysam.hchirinos.transport.common.BookingsPassengersPdfGenerator
import com.skysam.hchirinos.transport.common.Classes
import com.skysam.hchirinos.transport.dataClasses.Booking
import com.skysam.hchirinos.transport.databinding.FragmentBookingBinding
import com.skysam.hchirinos.transport.ui.common.WrapLayoutManager
import com.skysam.hchirinos.transport.ui.passengers.PassengersBottomSheet
import com.skysam.hchirinos.transport.ui.payment.AddPaymentDialog
import com.skysam.hchirinos.transport.ui.refund.AddRefundDialog
import java.io.IOException

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
    override fun editPassengers(booking: Booking) {
        viewModel.viewBooking(booking)
        PassengersBottomSheet().show(requireActivity().supportFragmentManager, tag)
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
            R.id.app_bar_filter -> {
                filter()
                true
            }
            R.id.app_bar_pdf -> {
                downloadBookingsPdf()
                true
            }
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

    private fun filter() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.layout_options_filter_bookings)
        bottomSheetDialog.dismissWithAnimation = true
        bottomSheetDialog.show()
        val viewSheet: View? = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)

        val btnPaids: MaterialCardView = viewSheet!!.findViewById(R.id.card_paids)
        val btnNotPaids: MaterialCardView = viewSheet.findViewById(R.id.card_not_paids)
        val btnAll: MaterialCardView = viewSheet.findViewById(R.id.card_all)

        btnPaids.setOnClickListener {
            bottomSheetDialog.hide()
            doFilter(true)
        }
        btnNotPaids.setOnClickListener {
            bottomSheetDialog.hide()
            doFilter(false)
        }
        btnAll.setOnClickListener {
            bottomSheetDialog.hide()
            val list: List<Booking> = bookings
            bookingAdapter.updateList(list)
        }
    }

    private fun doFilter(paids: Boolean) {
        val list = mutableListOf<Booking>()
        bookings.forEach {
            val diff = Classes.getTotalBooking(it.quantity) + Classes.totalRefunds(it.refunds) -
                    Classes.totalPayments(it.payments)
            if (diff <= 0.0 && paids) list.add(it)
            if (diff > 0.0 && !paids) list.add(it)
        }
        bookingAdapter.updateList(list)
    }

    private fun downloadBookingsPdf() {
        if (bookings.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.msg_no_bookings), Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val result = BookingsPassengersPdfGenerator.generate(
                bookings = bookings
            )

            val uri = savePdfToDownloads(result.fileName, result.bytes)

            Toast.makeText(requireContext(), getString(R.string.msg_pdf_saved), Toast.LENGTH_SHORT).show()

            sharePdf(uri)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), getString(R.string.msg_pdf_error), Toast.LENGTH_SHORT).show()
        }
    }

    private fun savePdfToDownloads(fileName: String, bytes: ByteArray): Uri {
        val resolver = requireContext().contentResolver

        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, "application/pdf")

            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            put(MediaStore.Downloads.IS_PENDING, 1)
        }

        val collection =
            MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

        val itemUri = resolver.insert(collection, contentValues)
            ?: throw IOException("No se pudo crear el archivo en Downloads")

        resolver.openOutputStream(itemUri, "w")?.use { out ->
            out.write(bytes)
            out.flush()
        } ?: throw IOException("No se pudo abrir OutputStream para el PDF")

        contentValues.clear()
        contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
        resolver.update(itemUri, contentValues, null, null)

        return itemUri
    }

    private fun sharePdf(uri: Uri) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(shareIntent, "Compartir PDF"))
    }
}