package com.skysam.hchirinos.transport.common

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.skysam.hchirinos.transport.BuildConfig
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

 fun convertDateToString(value: Date): String {
  return DateFormat.getDateInstance().format(value)
 }
}