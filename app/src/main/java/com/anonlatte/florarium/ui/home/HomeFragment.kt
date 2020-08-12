package com.anonlatte.florarium.ui.home

import android.os.Bundle
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import kotlinx.coroutines.runBlocking

class HomeFragment : Fragment() {
    private lateinit var plantsAdapter: PlantsAdapter
    private var tracker: SelectionTracker<Plant>? = null
    private var actionMode: ActionMode? = null
    private val viewModel by viewModels<HomeViewModel>()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        plantsAdapter = PlantsAdapter()
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
                        selectionTracker.addObserver(object :
                                SelectionTracker.SelectionObserver<Plant>() {
                                override fun onSelectionChanged() {
                                    super.onSelectionChanged()
                                    if (selectionTracker.hasSelection()) {
                                        if (actionMode == null) {
                                            callActionMode()
                                        }
                                    } else {
                                        if (actionMode != null) {
                                            callActionMode()
                                        }
                                    }
                                }
                            })
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

    private fun callActionMode() {
        if (actionMode == null) {
            actionMode = requireActivity().startActionMode(object : ActionMode.Callback {
                override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                    when (item.itemId) {
                        R.id.delete -> {
                            val selectedPlants = tracker?.selection?.map {
                                plantsAdapter.plantsList[plantsAdapter.plantsList.indexOf(it)]
                            }?.toList()
                            runBlocking {
                                viewModel.deletePlants(selectedPlants)
                            }.observe(
                                viewLifecycleOwner,
                                Observer {
                                    val toast =
                                        Toast.makeText(requireContext(), null, Toast.LENGTH_LONG)
                                    when {
                                        it <= 0 -> {
                                            toast.setText(R.string.error_plant_deletion)
                                        }
                                        it > 1 -> {
                                            toast.setText(R.string.successful_multiple_deletion)
                                        }
                                        else -> {
                                            toast.setText(
                                                getString(
                                                    R.string.successful_single_deletion,
                                                    selectedPlants?.get(0)?.name
                                                )
                                            )
                                        }
                                    }
                                    toast.show()
                                }
                            )
                            tracker?.clearSelection()
                        }
                    }
                    return true
                }

                override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                    mode.menuInflater.inflate(R.menu.main, menu)
                    return true
                }

                override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                    return false
                }

                override fun onDestroyActionMode(mode: ActionMode?) {
                    tracker?.clearSelection()
                    actionMode = null
                }
            })
        } else {
            actionMode!!.finish()
        }
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
