package com.skysam.hchirinos.transport.ui.payment

import com.skysam.hchirinos.transport.dataClasses.Payment

/**
 * Created by Hector Chirinos on 05/12/2022.
 */

interface OnClick {
 fun delete(payment: Payment)
}