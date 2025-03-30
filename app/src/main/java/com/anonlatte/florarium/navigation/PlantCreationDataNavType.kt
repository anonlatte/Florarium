package com.anonlatte.florarium.navigation

import android.os.Bundle
import androidx.navigation.NavType
import com.anonlatte.florarium.data.domain.PlantCreationData
import kotlinx.serialization.json.Json

val plantCreationDataNavType = object : NavType<PlantCreationData?>(isNullableAllowed = true) {
    override fun get(bundle: Bundle, key: String): PlantCreationData? =
        bundle.getString(key)?.let { Json.decodeFromString(PlantCreationData.serializer(), it) }

    override fun parseValue(value: String): PlantCreationData =
        Json.decodeFromString(PlantCreationData.serializer(), value)

    override fun put(bundle: Bundle, key: String, value: PlantCreationData?) =
        bundle.putString(key, Json.encodeToString(value))

    override fun serializeAsValue(value: PlantCreationData?): String =
        value?.let { Json.encodeToString(PlantCreationData.serializer(), it) } ?: ""
}
