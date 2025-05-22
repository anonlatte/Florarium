package com.anonlatte.florarium.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

data class TopLevelRoute<T : Any>(val name: String, val route: T, val icon: ImageVector)

val topLevelRoutes = listOf(
    TopLevelRoute("List", Screen.PlantsList, Icons.AutoMirrored.Filled.List),
    TopLevelRoute("Home", Screen.Home, Icons.Default.Home),
    TopLevelRoute("Profile", Screen.Profile, Icons.Default.Person)
)