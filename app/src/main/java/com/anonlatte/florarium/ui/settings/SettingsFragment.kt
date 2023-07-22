package com.anonlatte.florarium.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.anonlatte.florarium.R
import com.anonlatte.florarium.databinding.FragmentSettingsBinding
import com.anonlatte.florarium.extensions.collectWithLifecycle
import com.anonlatte.florarium.ui.settings.data.SettingId
import com.anonlatte.florarium.ui.settings.data.SettingsCommand
import com.anonlatte.florarium.ui.settings.data.SettingsItemElement
import com.anonlatte.florarium.ui.settings.recycler.SettingsAdapter
import com.google.android.material.timepicker.MaterialTimePicker
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModels()

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val settingsAdapter = SettingsAdapter(::navigateToSetting)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        initViews()
        viewModel.screenCommand.collectWithLifecycle(viewLifecycleOwner, ::handleCommand)
        return binding.root
    }

    private fun initViews() = with(binding) {
        initSettingsList()
    }

    private fun initSettingsList() {
        binding.rvSettings.adapter = settingsAdapter
        binding.rvSettings.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSettings.applyInsetter {
            type(navigationBars = true, statusBars = true) {
                padding()
            }
        }
        settingsAdapter.submitList(
            listOf(
                SettingsItemElement.SettingsHeader(getString(SettingId.NOTIFICATIONS.title)),
                SettingsItemElement.SettingsItem(
                    SettingId.NOTIFICATIONS,
                    getString(R.string.content_setting_notifications_time_to_notify),
                    SettingId.NOTIFICATIONS.icon
                )
            )
        )
    }

    private fun handleCommand(command: SettingsCommand) {
        when (command) {
            SettingsCommand.ErrorTimeUpdated -> TODO()
            SettingsCommand.SuccessTimeUpdated -> TODO()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvSettings.adapter = null
        _binding = null
    }

    private fun navigateToSetting(settingId: SettingId) {
        when (settingId) {
            SettingId.NOTIFICATIONS -> showNotificationTimeAlert()
        }
    }

    private fun showNotificationTimeAlert() {
        val timePicker = MaterialTimePicker.Builder()
            .build()
        timePicker.addOnPositiveButtonClickListener {
            viewModel.updateNotificationTime(timePicker.hour, timePicker.minute)
        }
        timePicker.show(childFragmentManager, "timePicker")
    }
}
