package com.skysam.hchirinos.transport.ui.payment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.skysam.hchirinos.transport.R
import com.skysam.hchirinos.transport.common.Classes
import com.skysam.hchirinos.transport.dataClasses.Payment

/**
 * Created by Hector Chirinos on 02/12/2022.
 */

class PaymentAdapter(private val payments: MutableList<Payment>, private val view: Boolean,
                     private val onClick: OnClick):
    RecyclerView.Adapter<PaymentAdapter.ViewHolder>() {
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_item_payment, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: PaymentAdapter.ViewHolder, position: Int) {
        val item = payments[position]
        holder.payer.text = context.getString(R.string.text_payer_by, item.payer)
        holder.receiver.text = context.getString(R.string.text_receiver_by, item.receiver)
        holder.date.text = context.getString(R.string.text_date_paid,
            Classes.convertDateToString(item.date))
        holder.amount.text = context.getString(R.string.text_amount_paid,
            Classes.convertDoubleToString(item.amount))

        if (view) holder.btnDelete.visibility = View.GONE

        holder.btnDelete.setOnClickListener {
            onClick.delete(item)
        }
    }

    override fun getItemCount(): Int = payments.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val payer: TextView = view.findViewById(R.id.tv_payer)
        val receiver: TextView = view.findViewById(R.id.tv_receiver)
        val date: TextView = view.findViewById(R.id.tv_date)
        val amount: TextView = view.findViewById(R.id.tv_amount)
        val btnDelete: ImageButton = view.findViewById(R.id.ib_delete)
    }
}