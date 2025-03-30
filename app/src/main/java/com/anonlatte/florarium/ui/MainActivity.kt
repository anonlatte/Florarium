package com.anonlatte.florarium.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.anonlatte.florarium.data.domain.CareTask
import com.anonlatte.florarium.data.domain.Plant
import com.anonlatte.florarium.data.domain.PlantCreationData
import com.anonlatte.florarium.navigation.Screen
import com.anonlatte.florarium.navigation.plantCreationDataNavType
import com.anonlatte.florarium.ui.creation.AddPlantScreen
import com.anonlatte.florarium.ui.home.HomeScreen
import com.anonlatte.florarium.ui.theme.PlantCareAppTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID
import kotlin.reflect.typeOf

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            val navController = rememberNavController()
            PlantCareAppTheme {
                MainActivityContent(navController)
            }
        }
    }

    @Composable
    private fun PlantCareAppNavHost(navController: NavHostController, padding: PaddingValues) {
        NavHost(
            navController = navController,
            startDestination = Screen.Home,
            modifier = Modifier.padding(padding)
        ) {
            home(navController)
            addPlant(navController)
        }
    }

    @Composable
    fun MainActivityContent(navController: NavHostController) {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = false,
                        onClick = { /* TODO: navController.navigate(Screen.PlantsList.route) */ },
                        icon = {
                            Icon(
                                Icons.AutoMirrored.Filled.List,
                                contentDescription = "Plants"
                            )
                        },
                        label = { Text("Plants") }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { /* TODO: general info */ },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { /* TODO: navController.navigate(Screen.Profile.route) */ },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                        label = { Text("Profile") }
                    )
                }
            }
        ) { padding ->
            PlantCareAppNavHost(navController, padding)
        }
    }

}

private fun NavGraphBuilder.home(navController: NavHostController) {
    composable<Screen.Home> {
        HomeScreen(
            onAddPlant = {
                navController.navigate(
                    Screen.AddPlant(
                        PlantCreationData(
                            plant = Plant(
                                id = UUID.randomUUID().hashCode().toLong(),
                                name = "New Plant",
                                imageUri = "",
                                createdAt = System.currentTimeMillis()
                            ),
                            careTasks = listOf(
                                CareTask.Watering("Watering", 7),
                                CareTask.Spraying("Spraying", 14),
                                CareTask.Fertilizing("Fertilizing", 30),
                                CareTask.Rotating("Rotating", 365)
                            )
                        )
                    )
                )
            }
        )
    }
}

private fun NavGraphBuilder.addPlant(navController: NavHostController) {
    composable<Screen.AddPlant>(
        typeMap = mapOf(typeOf<PlantCreationData?>() to plantCreationDataNavType)
    ) { backStackEntry ->
        val addPlantScreen = backStackEntry.toRoute<Screen.AddPlant>()
        val plantData = addPlantScreen.plantData ?: PlantCreationData(
            plant = Plant(
                id = UUID.randomUUID().hashCode().toLong(),
                name = "",
                imageUri = "",
                createdAt = System.currentTimeMillis()
            ),
            careTasks = listOf()
        )
        AddPlantScreen(
            plantData = plantData,
            viewModel = hiltViewModel(),
            onBack = { navController.navigateUp() }
        )
    }
}
