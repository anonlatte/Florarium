package com.anonlatte.florarium.data.domain

import android.os.Parcelable
import com.anonlatte.florarium.data.db.model.CareHolderEntity
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Parcelize
@Serializable
data class CareHolder(
    val id: Long = 0,
    val plantId: Long = 0,
    val careTasks: List<CareTask> = emptyList(),
    val wateredAt: Long = 0,
    val sprayedAt: Long = 0,
    val fertilizedAt: Long = 0,
    val rotatedAt: Long = 0,
) : Parcelable {

    companion object {

        fun CareHolder.toEntity(): CareHolderEntity {
            return CareHolderEntity(
                id = id,
                plantId = plantId,
                careTasks = Json.encodeToString(careTasks),
                wateredAt = wateredAt,
                sprayedAt = sprayedAt,
                fertilizedAt = fertilizedAt,
                rotatedAt = rotatedAt,
            )
        }
    }
}