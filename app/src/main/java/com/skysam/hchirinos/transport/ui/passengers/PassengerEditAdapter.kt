package com.skysam.hchirinos.transport.ui.passengers

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.skysam.hchirinos.transport.R
import com.skysam.hchirinos.transport.dataClasses.PassengerRow

/**
 * Created by Hector Chirinos in the home office on 27 ene. 2026
 */
class PassengerEditAdapter(
    private val onChanged: () -> Unit,
    private val onDeleteExtra: (position: Int) -> Unit
) : RecyclerView.Adapter<PassengerEditAdapter.VH>() {

    private val rows = mutableListOf<PassengerRow>()
    private val docTypes = listOf("V", "E", "P")

    fun setRows(newRows: List<PassengerRow>) {
        rows.clear()
        rows.addAll(newRows)
        notifyDataSetChanged()
        onChanged()
    }

    fun getRows(): List<PassengerRow> = rows

    fun addRow(row: PassengerRow) {
        rows.add(row)
        notifyItemInserted(rows.lastIndex)
        onChanged()
    }

    fun removeAt(position: Int) {
        if (position !in rows.indices) return
        rows.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, rows.size - position)
        onChanged()
    }

    fun recomputeExtraFlags(expectedQuantity: Int) {
        rows.forEachIndexed { index, r -> r.isExtra = index >= expectedQuantity }
        notifyDataSetChanged()
        onChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_passenger_edit, parent, false)
        return VH(v)
    }

    override fun getItemCount(): Int = rows.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val row = rows[position]
        holder.bind(row, position, docTypes, onChanged, onDeleteExtra)
    }

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        private val tvLabel: TextView = view.findViewById(R.id.tvLabel)
        private val tvExtra: TextView = view.findViewById(R.id.tvExtra)
        private val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
        private val etFullName: TextInputEditText = view.findViewById(R.id.etFullName)
        private val etDocNumber: TextInputEditText = view.findViewById(R.id.etDocNumber)
        private val actvDocType: MaterialAutoCompleteTextView = view.findViewById(R.id.actvDocType)

        fun bind(
            row: PassengerRow,
            position: Int,
            docTypes: List<String>,
            onChanged: () -> Unit,
            onDeleteExtra: (position: Int) -> Unit
        ) {
            tvLabel.text = "Pasajero ${position + 1}"

            tvExtra.visibility = if (row.isExtra) View.VISIBLE else View.GONE
            btnDelete.visibility = if (row.isExtra) View.VISIBLE else View.GONE

            // Dropdown tipo doc
            if (actvDocType.adapter == null) {
                actvDocType.setAdapter(
                    ArrayAdapter(
                        actvDocType.context,
                        android.R.layout.simple_list_item_1,
                        docTypes
                    )
                )
            }
            actvDocType.setOnClickListener(null)
            actvDocType.setOnItemClickListener(null)
            etDocNumber.onFocusChangeListener = null
            etDocNumber.setOnClickListener(null)

            // Evitar disparos raros por reciclaje
            if (etFullName.tag is TextWatcher) etFullName.removeTextChangedListener(etFullName.tag as TextWatcher)
            if (etDocNumber.tag is TextWatcher) etDocNumber.removeTextChangedListener(etDocNumber.tag as TextWatcher)

            etFullName.setText(row.passenger.fullName)
            etDocNumber.setText(formatWithDots(row.passenger.documentNumber))

            val currentType = row.passenger.documentType.ifBlank { "V" }
            actvDocType.setText(currentType, false)

            val w1 = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    row.passenger.fullName = s?.toString() ?: ""
                    onChanged()
                }
                override fun afterTextChanged(s: Editable?) {}
            }

            val w2 = object : TextWatcher {
                private var selfChange = false

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // No hagas setText aquí
                }

                override fun afterTextChanged(s: Editable?) {
                    if (selfChange) return

                    val raw = s?.toString().orEmpty()
                    val digits = raw.filter { it.isDigit() }

                    // Guardar RAW (sin puntos)
                    row.passenger.documentNumber = digits

                    val formatted = formatWithDots(digits)
                    if (formatted != raw) {
                        selfChange = true
                        etDocNumber.setText(formatted)
                        etDocNumber.setSelection(formatted.length)
                        selfChange = false
                    } else {
                        // cursor al final
                        if ((etDocNumber.selectionStart != raw.length) || (etDocNumber.selectionEnd != raw.length)) {
                            etDocNumber.setSelection(raw.length)
                        }
                    }

                    onChanged()
                }
            }

            etDocNumber.setOnClickListener {
                etDocNumber.setSelection(etDocNumber.text?.length ?: 0)
            }
            etDocNumber.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) etDocNumber.setSelection(etDocNumber.text?.length ?: 0)
            }

            etFullName.addTextChangedListener(w1)
            etDocNumber.addTextChangedListener(w2)
            etFullName.tag = w1
            etDocNumber.tag = w2

            actvDocType.setOnClickListener {
                actvDocType.showDropDown()
            }
            actvDocType.setOnItemClickListener { _, _, _, _ ->
                row.passenger.documentType = actvDocType.text?.toString()?.ifBlank { "V" } ?: "V"
                onChanged()
            }

            btnDelete.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) onDeleteExtra(pos)
            }
        }

        private fun formatWithDots(digits: String): String {
            if (digits.isBlank()) return ""
            val clean = digits.filter { it.isDigit() }
            // Agrupa de derecha a izquierda en bloques de 3
            val rev = clean.reversed()
            val chunks = rev.chunked(3).joinToString(".")
            return chunks.reversed()
        }
    }

}
