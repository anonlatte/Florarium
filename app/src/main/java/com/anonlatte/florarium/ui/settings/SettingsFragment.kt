package com.anonlatte.florarium.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.anonlatte.florarium.databinding.FragmentSettingsBinding
import com.anonlatte.florarium.ui.settings.data.SettingId
import com.anonlatte.florarium.ui.settings.data.SettingsItemElement
import com.anonlatte.florarium.ui.settings.recycler.SettingsAdapter
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
class SettingsFragment : Fragment() {

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
        return binding.root
    }

    private fun initViews() = with(binding) {
        initSettingsList()
    }

    private fun initSettingsList() {
        binding.rvSettings.adapter = settingsAdapter
        binding.rvSettings.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSettings.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
        binding.rvSettings.applyInsetter {
            type(navigationBars = true, statusBars = true) {
                padding()
            }
        }
        settingsAdapter.submitList(
            listOf(
                SettingsItemElement.SettingsItem(
                    SettingId.NOTIFICATIONS,
                    getString(SettingId.NOTIFICATIONS.title),
                    SettingId.NOTIFICATIONS.icon
                )
            )
        )
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
            SettingId.NOTIFICATIONS -> TODO()
        }
    }
}

