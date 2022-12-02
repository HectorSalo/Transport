package com.skysam.hchirinos.transport.ui.init

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.skysam.hchirinos.transport.common.Constants
import com.skysam.hchirinos.transport.common.InitSession

/**
 * Created by Hector Chirinos on 30/11/2022.
 */

class InitViewModel: ViewModel() {
    private val _numberPass = MutableLiveData<String>().apply { value = "" }
    val numberPass: LiveData<String> get() = _numberPass

    private val _passAccept = MutableLiveData<Boolean>()
    val passAccept: LiveData<Boolean> get() = _passAccept

    fun addNewNumber(number: Int) {
        if (_numberPass.value!!.length < 4) {
            val newString = "${_numberPass.value}$number"
            _numberPass.value = newString
            if (_numberPass.value!!.length == 4) {
                _passAccept.value = _numberPass.value!! == Constants.PIN_INIT
            }
        }
    }

    fun deleteNumber() {
        if (_numberPass.value!!.isNotEmpty()) {
            _numberPass.value = _numberPass.value?.substring(0, _numberPass.value!!.length - 1)
        }
    }

    fun initSession(): LiveData<String?> {
        return InitSession.initSession().asLiveData()
    }
}