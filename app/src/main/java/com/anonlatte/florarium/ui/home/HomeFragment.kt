package com.anonlatte.florarium.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.anonlatte.florarium.adapters.PlantsAdapter
import com.anonlatte.florarium.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private val viewModel by viewModels<HomeViewModel>()
    private lateinit var plantsAdapter: PlantsAdapter
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        initializeAdapter()
        subscribeUI()
        return binding.root
    }

    private fun subscribeUI() {
        viewModel.plantsList.observe(viewLifecycleOwner, Observer {
            plantsAdapter.plantsList = it
        })
    }

    private fun initializeAdapter() {
        plantsAdapter = PlantsAdapter(viewModel.plantsList.value!!)
        binding.plantsList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = plantsAdapter
        }
    }
}