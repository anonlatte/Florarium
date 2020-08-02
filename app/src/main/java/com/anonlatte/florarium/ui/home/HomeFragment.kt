package com.anonlatte.florarium.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.anonlatte.florarium.R
import com.anonlatte.florarium.adapters.PlantsAdapter
import com.anonlatte.florarium.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private val viewModel by viewModels<HomeViewModel>()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val plantsAdapter = PlantsAdapter()
        setupRecyclerView(plantsAdapter)
        subscribeUI(plantsAdapter)

        binding.plantAddButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_creationFragment)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun subscribeUI(plantsAdapter: PlantsAdapter) {
        viewModel.plantsList.observe(
            viewLifecycleOwner,
            Observer { plants ->
                plantsAdapter.setPlants(plants)
            }
        )
    }

    private fun setupRecyclerView(plantsAdapter: PlantsAdapter) {
        binding.plantsList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = plantsAdapter
        }
    }
}
