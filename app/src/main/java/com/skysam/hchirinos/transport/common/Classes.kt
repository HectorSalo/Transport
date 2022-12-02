package com.skysam.hchirinos.transport.common

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.skysam.hchirinos.transport.BuildConfig
import com.skysam.hchirinos.transport.dataClasses.Booking
import java.text.Collator
import java.text.DateFormat
import java.util.*

/**
 * Created by Hector Chirinos on 01/12/2022.
 */

object Classes {
 fun getEnviroment(): String {
  return BuildConfig.BUILD_TYPE
 }

 fun close(view: View) {
  val imn = Transport.Transport.getContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
  imn.hideSoftInputFromWindow(view.windowToken, 0)
 }

 fun convertDoubleToString(value: Double): String {
  return String.format(Locale.GERMANY, "%,.2f", value)
 }

 fun convertDateToString(value: Date): String {
  return DateFormat.getDateInstance().format(value)
 }

 fun organizedAlphabeticList(list: MutableList<Booking>): MutableList<Booking> {
  Collections.sort(list, object : Comparator<Booking> {
   var collator = Collator.getInstance()
   override fun compare(p0: Booking?, p1: Booking?): Int {
    return collator.compare(p0?.name, p1?.name)
   }

  })
  return list
 }
}