package com.skysam.hchirinos.transport.common

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.skysam.hchirinos.transport.dataClasses.Booking
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Created by Hector Chirinos in the home office on 3 feb. 2026
 */
object BookingsPassengersPdfGenerator {
    data class PdfBytesResult(
        val fileName: String,
        val bytes: ByteArray
    )

    fun generate(
        bookings: List<Booking>
    ): PdfBytesResult {

        val now = Date()
        val fileTs = SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(now)
        val fileName = "Reservas_Pasajeros_$fileTs.pdf"

        val document = PdfDocument()

        // A4 aprox en points (72dpi)
        val pageWidth = 595
        val pageHeight = 842

        val margin = 40
        val lineH = 18
        val maxY = pageHeight - margin

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 12f
            color = Color.BLACK
        }
        val paintBold = Paint(paint).apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val paintSmall = Paint(paint).apply {
            textSize = 10f
        }

        val genFmt = SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault())

        var pageNumber = 1
        var y = 0

        fun startNewPage(): PdfDocument.Page {
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas

            // Header general
            val titleY = (margin + 14).toFloat()
            canvas.drawText("Lista de pasajeros", margin.toFloat(), titleY, paintBold)
            canvas.drawText("Generado: ${genFmt.format(now)}", margin.toFloat(), (margin + 32).toFloat(), paintSmall)
            canvas.drawLine(
                margin.toFloat(),
                (margin + 42).toFloat(),
                (pageWidth - margin).toFloat(),
                (margin + 42).toFloat(),
                paint
            )

            y = margin + 62
            return page
        }

        fun ensureSpace(
            currentPage: PdfDocument.Page,
            neededHeight: Int
        ): PdfDocument.Page {
            if (y + neededHeight <= maxY) return currentPage
            document.finishPage(currentPage)
            pageNumber++
            return startNewPage()
        }

        var page = startNewPage()
        var canvas: Canvas?

        bookings.forEachIndexed { index, booking ->

            // --- Bloque reserva (titulo + meta) ---
            page = ensureSpace(page, neededHeight = lineH * 3)
            canvas = page.canvas

            val bookingTitle = "${index + 1}. Reserva: ${booking.name.ifBlank { "-" }}"
            canvas.drawText(bookingTitle, margin.toFloat(), y.toFloat(), paintBold)
            y += lineH

            // Header tabla pasajeros
            canvas.drawText("Nombre", margin.toFloat(), y.toFloat(), paintBold)
            canvas.drawText("Documento", (pageWidth - margin - 180).toFloat(), y.toFloat(), paintBold)
            y += lineH

            // Separador
            canvas.drawLine(
                margin.toFloat(),
                (y - 10).toFloat(),
                (pageWidth - margin).toFloat(),
                (y - 10).toFloat(),
                paint
            )

            // --- Pasajeros ---
            val passengers = booking.passengers

            if (passengers.isEmpty()) {
                page = ensureSpace(page, neededHeight = lineH)
                canvas = page.canvas

                canvas.drawText("(Sin pasajeros)", margin.toFloat(), y.toFloat(), paint)
                y += lineH
            } else {
                passengers.forEach { p ->
                    page = ensureSpace(page, neededHeight = lineH)
                    canvas = page.canvas

                    val fullName = p.fullName.trim().ifEmpty { "-" }
                    val docType = p.documentType.trim().ifEmpty { "-" }
                    val docNum = p.documentNumber.trim().ifEmpty { "-" }
                    val doc = "$docType-$docNum"

                    canvas.drawText(fullName, margin.toFloat(), y.toFloat(), paint)
                    canvas.drawText(doc, (pageWidth - margin - 180).toFloat(), y.toFloat(), paint)
                    y += lineH
                }
            }

            // Espacio entre reservas
            y += 10
        }

        document.finishPage(page)

        val bytes = ByteArrayOutputStream().use { out ->
            document.writeTo(out)
            document.close()
            out.toByteArray()
        }

        return PdfBytesResult(fileName, bytes)
    }
}