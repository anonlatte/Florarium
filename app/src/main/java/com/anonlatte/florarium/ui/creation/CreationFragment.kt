package com.anonlatte.florarium.ui.creation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.anonlatte.florarium.databinding.FragmentPlantCreationBinding

class CreationFragment : Fragment() {
    private val viewModel by viewModels<CreationViewModel>()
    private lateinit var binding: FragmentPlantCreationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlantCreationBinding.inflate(inflater, container, false)
        return binding.root
    }
}