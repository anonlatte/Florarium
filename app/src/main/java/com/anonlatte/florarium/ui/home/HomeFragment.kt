package com.anonlatte.florarium.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anonlatte.florarium.R
import com.anonlatte.florarium.adapters.PlantsAdapter
import com.anonlatte.florarium.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var plantsAdapter: PlantsAdapter? = null
    private var recentPlantsAdapter: PlantsAdapter? = null
    private val viewModel by viewModels<HomeViewModel>()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        plantsAdapter = PlantsAdapter().also { setupPlantsRecyclerView(it) }
        recentPlantsAdapter = PlantsAdapter().also { setupRecentPlantsRecyclerView(it) }

        binding.plantAddButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_creationFragment)
        }

        subscribeUI()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        plantsAdapter = null
        recentPlantsAdapter = null
    }

    private fun subscribeUI() {
        viewModel.plantsList.observe(viewLifecycleOwner, {
            plantsAdapter!!.setPlants(it)
        })
        viewModel.recentPlantsList.observe(viewLifecycleOwner, {
            recentPlantsAdapter!!.setPlants(it)
        })
        viewModel.regularSchedulesList.observe(viewLifecycleOwner, {
            plantsAdapter!!.setSchedules(it)
        })
        viewModel.regularSchedulesList.observe(viewLifecycleOwner, {
            recentPlantsAdapter!!.setSchedules(it)
        })
    }

    private fun setupRecentPlantsRecyclerView(plantsAdapter: PlantsAdapter) {
        binding.recyclerRecentPlants.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = RecyclerView.HORIZONTAL
            }
            adapter = plantsAdapter
        }
    }

    private fun setupPlantsRecyclerView(plantsAdapter: PlantsAdapter) {
        binding.recyclerAllPlants.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = RecyclerView.HORIZONTAL
            }
            adapter = plantsAdapter
        }
    }
}
