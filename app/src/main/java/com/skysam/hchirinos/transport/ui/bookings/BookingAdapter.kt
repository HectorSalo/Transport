package com.skysam.hchirinos.transport.ui.bookings

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.skysam.hchirinos.transport.R
import com.skysam.hchirinos.transport.common.Classes
import com.skysam.hchirinos.transport.dataClasses.Booking

/**
 * Created by Hector Chirinos on 01/12/2022.
 */

class BookingAdapter(private val onClick: OnClick):
    RecyclerView.Adapter<BookingAdapter.ViewHolder>() {
    lateinit var context: Context
    private var bookings = listOf<Booking>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_item_booking, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingAdapter.ViewHolder, position: Int) {
        val item = bookings[position]
        holder.name.text = item.name
        holder.quantity.text = context.getString(R.string.text_quantity_seats_item,
            item.quantity.toString())

        val diff = Classes.getTotalBooking(item.quantity) + Classes.totalRefunds(item.refunds) -
                Classes.totalPayments(item.payments)
        if (diff == 0.0) holder.image.setImageResource(R.drawable.ic_paid_24)
        if (diff > 0.0) holder.image.setImageResource(R.drawable.ic_debt_24)
        if (diff < 0.0) holder.image.setImageResource(R.drawable.ic_refund_24)


        holder.menu.setOnClickListener {
            val popMenu = PopupMenu(context, holder.menu)
            popMenu.inflate(R.menu.menu_bookings_item)
            if (diff == 0.0) {
                popMenu.menu.getItem(0).isVisible = false
                popMenu.menu.getItem(1).isVisible = false
            }
            if (diff > 0.0) {
                popMenu.menu.getItem(1).isVisible = false
            }
            if (diff < 0.0) {
                popMenu.menu.getItem(0).isVisible = false
            }
            popMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_edit -> onClick.edit(item)
                    R.id.menu_delete -> onClick.delete(item)
                    R.id.menu_add_payment -> onClick.addPayment(item)
                    R.id.menu_add_refund -> onClick.addRefund(item)
                }
                false
            }
            popMenu.show()
        }

        holder.card.setOnClickListener { onClick.view(item) }
    }

    override fun getItemCount(): Int = bookings.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_name)
        val quantity: TextView = view.findViewById(R.id.tv_quantity)
        val menu: TextView = view.findViewById(R.id.tv_menu)
        val image: ImageView = view.findViewById(R.id.imageView)
        val card: MaterialCardView = view.findViewById(R.id.card)
    }

    fun updateList(newList: List<Booking>) {
        val diffUtil = BookingDiffUtil(bookings, newList)
        val result = DiffUtil.calculateDiff(diffUtil)
        bookings = newList
        result.dispatchUpdatesTo(this)
    }
}