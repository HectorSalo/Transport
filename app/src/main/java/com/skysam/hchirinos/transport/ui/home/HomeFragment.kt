package com.skysam.hchirinos.transport.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.skysam.hchirinos.transport.databinding.FragmentHomeBinding
import com.skysam.hchirinos.transport.ui.bookings.newBooking.NewBookingActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.button.setOnClickListener { startActivity(Intent(requireContext(), NewBookingActivity::class.java)) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}