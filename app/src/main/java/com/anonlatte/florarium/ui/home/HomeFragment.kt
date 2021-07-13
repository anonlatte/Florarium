package com.anonlatte.florarium.ui.home

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import com.anonlatte.florarium.R
import com.anonlatte.florarium.data.model.Plant
import com.anonlatte.florarium.data.model.PlantWithSchedule
import com.anonlatte.florarium.databinding.FragmentHomeBinding
import com.anonlatte.florarium.extensions.appComponent
import com.anonlatte.florarium.extensions.launchWhenStarted
import com.anonlatte.florarium.ui.home.adapters.PlantItemDetailsLookup
import com.anonlatte.florarium.ui.home.adapters.PlantKeyProvider
import com.anonlatte.florarium.ui.home.adapters.PlantsAdapter
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class HomeFragment : Fragment() {
    private lateinit var plantsAdapter: PlantsAdapter
    private lateinit var tracker: SelectionTracker<Long>
    private var actionMode: ActionMode? = null

    @Inject
    lateinit var viewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val trackerObserver = object : SelectionTracker.SelectionObserver<Long>() {
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        subscribeUI()
        setupSelectionTracker()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        binding.plantAddButton.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_creation)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.plantsList.adapter = null
        _binding = null
    }

    private fun subscribeUI() {
        viewModel.plantsToSchedules.onEach {
            plantsAdapter.submitList(it)
        }.launchWhenStarted(lifecycleScope)
    }

    private fun callActionMode() {
        if (actionMode == null) {
            actionMode = requireActivity().startActionMode(object : ActionMode.Callback {
                override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                    when (item.itemId) {
                        R.id.delete -> deleteSelectedPlants()
                    }
                    return true
                }

                override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                    mode.menuInflater.inflate(R.menu.main, menu)
                    return true
                }

                override fun onPrepareActionMode(mode: ActionMode, menu: Menu) = false
                override fun onDestroyActionMode(mode: ActionMode?) {
                    tracker.clearSelection()
                    actionMode = null
                }
            })
        } else {
            actionMode?.finish()
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

    private fun setupRecyclerView() {
        plantsAdapter = PlantsAdapter { plant, schedule ->
            val actionHomeToCreation = HomeFragmentDirections.actionHomeToCreation(plant, schedule)
            findNavController().navigate(actionHomeToCreation)
        }
        binding.plantsList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = plantsAdapter
        }
    }

    private fun setupSelectionTracker() {
        tracker = SelectionTracker.Builder(
            "plant-selection",
            binding.plantsList,
            PlantKeyProvider(plantsAdapter),
            PlantItemDetailsLookup(binding.plantsList),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()
        tracker.addObserver(trackerObserver)
        plantsAdapter.setTracker(tracker)
    }
}

