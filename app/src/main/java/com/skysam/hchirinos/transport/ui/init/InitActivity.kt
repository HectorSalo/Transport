package com.skysam.hchirinos.transport.ui.init

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.material.snackbar.Snackbar
import com.skysam.hchirinos.transport.MainActivity
import com.skysam.hchirinos.transport.R
import com.skysam.hchirinos.transport.common.Constants
import com.skysam.hchirinos.transport.common.InitSession
import com.skysam.hchirinos.transport.databinding.ActivityInitBinding

class InitActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityInitBinding
    private val viewModel: InitViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityInitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)

        binding.button0.setOnClickListener(this)
        binding.button1.setOnClickListener(this)
        binding.button2.setOnClickListener(this)
        binding.button3.setOnClickListener(this)
        binding.button4.setOnClickListener(this)
        binding.button5.setOnClickListener(this)
        binding.button6.setOnClickListener(this)
        binding.button7.setOnClickListener(this)
        binding.button8.setOnClickListener(this)
        binding.button9.setOnClickListener(this)
        binding.buttonDelete.setOnClickListener(this)

        loadViewModel()
    }

    private fun loadViewModel() {
        viewModel.numberPass.observe(this) {
            binding.tvPin.text = it
        }
        viewModel.passAccept.observe(this) {
            if (it) {
                viewModel.initSession().observe(this) { msg ->
                    if (!msg.isNullOrEmpty()) {
                        if (msg == Constants.OK) {
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
                    } else Snackbar.make(binding.root, R.string.error_init_session, Snackbar.LENGTH_LONG).show()
                }
            } else {
                Snackbar.make(binding.root, R.string.error_pin_code, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun onClick(component: View?) {
        when(component?.id) {
            R.id.button0 -> {viewModel.addNewNumber(0)}
            R.id.button1 -> {viewModel.addNewNumber(1)}
            R.id.button2 -> {viewModel.addNewNumber(2)}
            R.id.button3 -> {viewModel.addNewNumber(3)}
            R.id.button4 -> {viewModel.addNewNumber(4)}
            R.id.button5 -> {viewModel.addNewNumber(5)}
            R.id.button6 -> {viewModel.addNewNumber(6)}
            R.id.button7 -> {viewModel.addNewNumber(7)}
            R.id.button8 -> {viewModel.addNewNumber(8)}
            R.id.button9 -> {viewModel.addNewNumber(9)}
            R.id.button_delete -> {viewModel.deleteNumber()}
        }
    }

    override fun onStart() {
        super.onStart()
        if (InitSession.getUser() != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}