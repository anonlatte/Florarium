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

        with(navigateToPlantsList()) {
            binding.textInYourPlantsShowAll.setOnClickListener(this)
            binding.textInRecentShowAll.setOnClickListener(this)
            binding.textYourPlants.setOnClickListener(this)
        }

        binding.buttonAddPlant.setOnClickListener {
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
        viewModel.plantsList.observe(viewLifecycleOwner, { plants ->
            if (plants.isNotEmpty()) {
                binding.textInRecentShowAll.visibility = View.VISIBLE
                binding.textInYourPlantsShowAll.visibility = View.VISIBLE
                val sublistAmount = if (plants.size > 5) {
                    5
                } else {
                    plants.size
                }
                plantsAdapter!!.setPlants(plants.subList(0, sublistAmount))
                recentPlantsAdapter!!.setPlants(
                    plants.sortedByDescending { it.updatedAt }.subList(0, sublistAmount)
                )
            } else {
                binding.textInRecentShowAll.visibility = View.GONE
                binding.textInYourPlantsShowAll.visibility = View.GONE
            }
        })
    }

    private fun navigateToPlantsList() = View.OnClickListener {
        findNavController().navigate(R.id.action_homeFragment_to_plantsListFragment)
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
