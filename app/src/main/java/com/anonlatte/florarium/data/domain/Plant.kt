package com.anonlatte.florarium.data.domain

import android.os.Parcelable
import com.anonlatte.florarium.data.db.model.PlantEntity
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Plant(
    val id: Long = 0,
    val name: String = "",
    val imageUri: String = "",
    val createdAt: Long? = null,
) : Parcelable {

    companion object {

        fun Plant.toEntity() = PlantEntity(
            id = id,
            name = name,
            imageUri = imageUri,
            createdAt = createdAt,
        )
    }
}