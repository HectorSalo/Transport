package com.skysam.hchirinos.transport.ui.bookings

import androidx.recyclerview.widget.DiffUtil
import com.skysam.hchirinos.transport.dataClasses.Booking

/**
 * Created by Hector Chirinos on 26/06/2023.
 */

class BookingDiffUtil(private val oldList: List<Booking>, private val newList: List<Booking>):
 DiffUtil.Callback() {
 override fun getOldListSize(): Int = oldList.size

 override fun getNewListSize(): Int = newList.size

 override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
  return oldList[oldItemPosition] == newList[newItemPosition]
 }

 override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
  return newList.contains(oldList[oldItemPosition])
 }
}