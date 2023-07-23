package com.anonlatte.florarium.data.domain

import android.os.Parcelable
import com.anonlatte.florarium.data.db.model.CareHolderEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class CareHolder(
    val id: Long = 0,
    val wateredAt: Long = 0,
    val sprayedAt: Long = 0,
    val fertilizedAt: Long = 0,
    val rotatedAt: Long = 0,
) : Parcelable {

    companion object {

        fun CareHolder.toEntity(): CareHolderEntity {
            return CareHolderEntity(
                id = id,
                wateredAt = wateredAt,
                sprayedAt = sprayedAt,
                fertilizedAt = fertilizedAt,
                rotatedAt = rotatedAt,
            )
        }
    }
}