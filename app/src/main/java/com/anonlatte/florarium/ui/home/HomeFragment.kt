package com.anonlatte.florarium.ui.home

import android.os.Bundle
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import com.anonlatte.florarium.R
import com.anonlatte.florarium.adapters.PlantItemDetailsLookup
import com.anonlatte.florarium.adapters.PlantKeyProvider
import com.anonlatte.florarium.adapters.PlantsAdapter
import com.anonlatte.florarium.data.model.PlantWithSchedule
import com.anonlatte.florarium.databinding.FragmentHomeBinding
import com.anonlatte.florarium.db.models.Plant
import com.anonlatte.florarium.extensions.launchWhenStarted
import kotlinx.coroutines.flow.onEach

class HomeFragment : Fragment() {
    private lateinit var plantsAdapter: PlantsAdapter
    private lateinit var tracker: SelectionTracker<Long>
    private var actionMode: ActionMode? = null
    private val viewModel by viewModels<HomeViewModel>()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val trackerObserver = object : SelectionTracker.SelectionObserver<Plant>() {
        override fun onSelectionChanged() {
            super.onSelectionChanged()
            if (tracker.hasSelection()) {
                if (actionMode == null) {
                    callActionMode()
                }
            } else {
                if (actionMode != null) {
                    callActionMode()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        plantsAdapter = PlantsAdapter { plant, schedule ->
            findNavController().navigate(
                R.id.action_homeFragment_to_creationFragment,
                bundleOf(
                    "plant" to plant,
                    "schedule" to schedule
                )
            )
        }.also {
            setupRecyclerView(it)
            subscribeUI(it)
        }
        setupSelectionTracker()

        binding.plantAddButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_creationFragment)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.plantsList.adapter = null
        _binding = null
    }

    private fun subscribeUI(plantsAdapter: PlantsAdapter) {
        viewModel.plantsToSchedules.onEach {
            plantsAdapter.submitList(it)
        }.launchWhenStarted(lifecycleScope)
    }

    private fun callActionMode() {
        if (actionMode == null) {
            actionMode = requireActivity().startActionMode(object : ActionMode.Callback {
                override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                    when (item.itemId) {
                        R.id.delete -> {
                            deleteSelectedPlants()
                        }
                    }
                    return true
                }

                override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                    mode.menuInflater.inflate(R.menu.main, menu)
                    return true
                }

                override fun onPrepareActionMode(mode: ActionMode, menu: Menu) = false
                override fun onDestroyActionMode(mode: ActionMode?) {
                    tracker?.clearSelection()
                    actionMode = null
                }
            })
        } else {
            actionMode!!.finish()
        }
    }

    private fun deleteSelectedPlants() {
        val selectedPlants: List<PlantWithSchedule> = tracker.selection.map {
            plantsAdapter.currentList[it.toInt()]
        }
        viewModel.deletePlants(selectedPlants).onEach { amount ->
            showToastDeleteInfo(selectedPlants.map { it.plant }, amount)
            tracker.clearSelection()
        }.launchWhenStarted(lifecycleScope)
    }

    private fun showToastDeleteInfo(selectedPlants: List<Plant>?, deletedCounter: Int) {
        val toastText = when {
            deletedCounter == 1 -> {
                getString(R.string.successful_single_deletion, selectedPlants?.get(0)?.name)
            }
            deletedCounter > 1 -> getString(R.string.successful_multiple_deletion)
            else -> getString(R.string.error_plant_deletion)
        }
        Toast.makeText(requireContext(), toastText, Toast.LENGTH_LONG).show()
    }

    private fun setupRecyclerView(plantsAdapter: PlantsAdapter) {
        binding.plantsList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = plantsAdapter
        }
    }

    private fun setupSelectionTracker() {
        SelectionTracker.Builder(
            "plant-selection",
            binding.plantsList,
            PlantKeyProvider(binding.plantsList),
            PlantItemDetailsLookup(binding.plantsList),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build().let {
            // it.addObserver(trackerObserver)
            tracker = it
            plantsAdapter.setTracker(tracker)
        }
    }
}

