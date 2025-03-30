package com.anonlatte.florarium.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Surface
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
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
                Surface {
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home
                    ) {
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
                }
            }
        }
    }
}
