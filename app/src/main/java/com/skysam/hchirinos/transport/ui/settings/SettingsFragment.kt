package com.skysam.hchirinos.transport.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import androidx.preference.SwitchPreferenceCompat
import com.skysam.hchirinos.transport.BuildConfig
import com.skysam.hchirinos.transport.R
import com.skysam.hchirinos.transport.common.InitSession
import com.skysam.hchirinos.transport.ui.init.InitActivity
import kotlinx.coroutines.launch

class SettingsFragment : PreferenceFragmentCompat() {
    private val viewModel: SettingsViewModel by activityViewModels()
    private lateinit var switchNotification: SwitchPreferenceCompat
    private lateinit var switchAssembly: SwitchPreferenceCompat

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        switchNotification = findPreference(getString(R.string.notification_key))!!
        switchAssembly = findPreference(getString(R.string.assembly_key))!!

        switchNotification.setOnPreferenceChangeListener { _, newValue ->
            val isOn = newValue as Boolean
            lifecycleScope.launch {
                viewModel.changeNotificationStatus(isOn)
            }
            true
        }

        switchAssembly.setOnPreferenceChangeListener { _, newValue ->
            val isOn = newValue as Boolean
            lifecycleScope.launch {
                viewModel.changeAssemblyStatus(isOn)
            }
            true
        }

        val versionPreferenceScreen = findPreference<PreferenceScreen>("name_version")
        versionPreferenceScreen?.title = getString(R.string.version_name, BuildConfig.VERSION_NAME)

        val signOutPreference: PreferenceScreen = findPreference("signOut")!!
        signOutPreference.setOnPreferenceClickListener {
            signOut()
            true
        }

        loadViewModels()
    }

    private fun loadViewModels() {
        viewModel.notificationActive.observe(viewLifecycleOwner) {
            switchNotification.isChecked = it
            val icon = if (it) R.drawable.ic_notifications_active_24 else R.drawable.ic_notifications_off_24
            switchNotification.setIcon(icon)
            switchNotification.title = if (it) getString(R.string.notification_title)
            else getString(R.string.notification_title_off)
        }

        viewModel.assemblyActive.observe(viewLifecycleOwner) {
            switchAssembly.isChecked = it
            val icon = if (it) R.drawable.ic_assembly_on_24 else R.drawable.ic_assembly_off_24
            switchAssembly.setIcon(icon)
            switchAssembly.title = if (it) getString(R.string.assembly_title)
            else getString(R.string.assembly_title_off)
        }
    }

    private fun signOut() {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_sign_out))
            .setMessage(getString(R.string.message_sign_out))
            .setPositiveButton(R.string.title_sign_out) { _, _ ->
                InitSession.getInstance()
                    .signOut()
                requireActivity().startActivity(Intent(requireContext(), InitActivity::class.java))
                requireActivity().finish()
            }
            .setNegativeButton(R.string.text_cancel, null)

        val dialog = builder.create()
        dialog.show()
    }
}