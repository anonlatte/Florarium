package com.anonlatte.florarium.ui.plants

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.anonlatte.florarium.R
import com.anonlatte.florarium.adapters.PlantsFullAdapter
import com.anonlatte.florarium.databinding.FragmentPlantsBinding

class PlantsFragment : Fragment() {
    private var plantsAdapter: PlantsFullAdapter? = null
    private val viewModel by viewModels<PlantsViewModel>()
    private var _binding: FragmentPlantsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlantsBinding.inflate(inflater, container, false)

        plantsAdapter = PlantsFullAdapter().also { setupPlantsRecyclerView(it) }

        binding.fabAddPlant.setOnClickListener {
            findNavController().navigate(R.id.action_plantsListFragment_to_creationFragment)
        }

        subscribeUI()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        plantsAdapter = null
    }

    private fun subscribeUI() {
        viewModel.plantsList.observe(viewLifecycleOwner, {
            plantsAdapter!!.setPlants(it)
        })
        viewModel.regularSchedulesList.observe(viewLifecycleOwner, {
            plantsAdapter!!.setSchedules(it)
        })
    }

    private fun setupPlantsRecyclerView(plantsAdapter: PlantsFullAdapter) {
        binding.recyclerPlants.apply {
            setHasFixedSize(true)
            adapter = plantsAdapter
        }
    }
}
