package com.anonlatte.florarium.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import com.anonlatte.florarium.R
import com.anonlatte.florarium.adapters.PlantItemDetailsLookup
import com.anonlatte.florarium.adapters.PlantKeyProvider
import com.anonlatte.florarium.adapters.PlantsAdapter
import com.anonlatte.florarium.databinding.FragmentHomeBinding
import com.anonlatte.florarium.db.models.Plant

class HomeFragment : Fragment() {
    private var tracker: SelectionTracker<Plant>? = null
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
        tracker = null
    }

    private fun subscribeUI(plantsAdapter: PlantsAdapter) {
        viewModel.plantsList.observe(
            viewLifecycleOwner,
            Observer { plants ->
                plantsAdapter.setPlants(plants)
                // FIXME Item change notification received for unknown item: Plant
                tracker = setupSelectionTracker(plants).also { selectionTracker ->
                    if (selectionTracker != null) {
                        plantsAdapter.setTracker(selectionTracker)
                    }
                }
            }
        )
        viewModel.regularSchedulesList.observe(
            viewLifecycleOwner,
            Observer { schedules ->
                plantsAdapter.setSchedules(schedules)
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

    private fun setupSelectionTracker(plants: List<Plant>): SelectionTracker<Plant>? {
        return SelectionTracker.Builder<Plant>(
            "plant-selection",
            binding.plantsList,
            PlantKeyProvider(plants),
            PlantItemDetailsLookup(binding.plantsList),
            StorageStrategy.createParcelableStorage(Plant::class.java)
        ).withSelectionPredicate(SelectionPredicates.createSelectAnything()).build()
    }
}
