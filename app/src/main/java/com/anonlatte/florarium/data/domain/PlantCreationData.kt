package com.anonlatte.florarium.data.domain

import kotlinx.serialization.Serializable

@Serializable
data class PlantCreationData(
    val plant: Plant,
    val careTasks: List<CareTask>,
)