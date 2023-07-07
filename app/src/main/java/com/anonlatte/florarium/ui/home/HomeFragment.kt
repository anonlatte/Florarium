package com.anonlatte.florarium.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.anonlatte.florarium.R
import com.anonlatte.florarium.databinding.FragmentHomeBinding
import com.anonlatte.florarium.extensions.collectWithLifecycle
import com.anonlatte.florarium.ui.home.adapters.PlantsAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var plantsAdapter: PlantsAdapter

    private val viewModel by viewModels<HomeViewModel>()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        subscribeUI()
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
        plantsAdapter = PlantsAdapter { plant, schedule ->
            val actionHomeToCreation = HomeFragmentDirections.actionHomeToCreation(plant, schedule)
            findNavController().navigate(actionHomeToCreation)
        }
        binding.plantsList.apply {
            setHasFixedSize(true)
            adapter = plantsAdapter
        }
    }
}

