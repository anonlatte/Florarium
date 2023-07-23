package com.anonlatte.florarium.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.anonlatte.florarium.R
import com.anonlatte.florarium.databinding.FragmentHomeBinding
import com.anonlatte.florarium.extensions.collectWithLifecycle
import com.anonlatte.florarium.ui.home.adapters.PlantsAdapter
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var plantsAdapter: PlantsAdapter

    private val viewModel by viewModels<HomeViewModel>()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            // TODO show specific screen to request notifications
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        subscribeUI()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationsPermissions()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        binding.plantAddButton.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_creation)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getPlantsToSchedules()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.plantsList.adapter = null
        _binding = null
    }

    private fun subscribeUI() {
        viewModel.screenState.collectWithLifecycle(this, ::renderState)
    }

    private fun renderState(screenState: HomeScreenState) {
        when (screenState) {
            is HomeScreenState.Default -> {
                plantsAdapter.submitList(screenState.plantsToSchedules)
            }

            else -> Unit
        }
        if (screenState == HomeScreenState.Loading) {
            binding.progressBar.show()
        } else {
            binding.progressBar.hide()
        }
    }

    private fun setupRecyclerView() {
        plantsAdapter = PlantsAdapter { plantWithSchedule ->
            val actionHomeToCreation =
                HomeFragmentDirections.actionHomeToCreation(plantWithSchedule)
            findNavController().navigate(actionHomeToCreation)
        }
        binding.plantsList.apply {
            setHasFixedSize(true)
            adapter = plantsAdapter
            applyInsetter {
                type(navigationBars = true, statusBars = true) {
                    padding()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationsPermissions() {
        when {
            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                // TODO show specific screen to request notifications
            }

            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED -> {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

