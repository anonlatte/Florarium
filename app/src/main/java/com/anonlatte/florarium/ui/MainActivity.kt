package com.anonlatte.florarium.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.anonlatte.florarium.navigation.Screen
import com.anonlatte.florarium.navigation.topLevelRoutes
import com.anonlatte.florarium.ui.creation.AddPlantScreen
import com.anonlatte.florarium.ui.home.HomeScreen
import com.anonlatte.florarium.ui.plants.PlantsListScreen
import com.anonlatte.florarium.ui.theme.PlantCareAppTheme
import dagger.hilt.android.AndroidEntryPoint

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
    fun MainActivityContent(navController: NavHostController) {
        Scaffold(
            bottomBar = {
                val navBackStackEntry = navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry.value?.destination

                if (currentDestination != null &&
                    currentDestination.route in topLevelRoutes.map { it.name }
                ) {
                    BottomNavigation {
                        topLevelRoutes.forEach { topLevelRoute ->
                            BottomNavigationItem(
                                selected = currentDestination.hierarchy.any {
                                    it.hasRoute(topLevelRoute.route::class)
                                },
                                onClick = {
                                    navController.navigate(topLevelRoute.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = topLevelRoute.icon,
                                        contentDescription = topLevelRoute.name
                                    )
                                },
                                label = { Text(topLevelRoute.name) })
                        }
                    }
                }
            }
        ) { padding ->
            PlantCareAppNavHost(navController, padding)
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
            plantsList(navController)
        }
    }
}

private fun NavGraphBuilder.home(navController: NavHostController) {
    composable<Screen.Home> {
        HomeScreen(
            onAddPlant = {
                navController.navigate(Screen.AddPlant())
            }
        )
    }
}

private fun NavGraphBuilder.addPlant(navController: NavHostController) {
    composable<Screen.AddPlant> { backStackEntry ->
        val addPlantScreen = backStackEntry.toRoute<Screen.AddPlant>()
        AddPlantScreen(
            plantId = addPlantScreen.plantId,
            viewModel = hiltViewModel(),
            onBack = { navController.navigateUp() }
        )
    }
}

private fun NavGraphBuilder.plantsList(navController: NavHostController) {
    composable<Screen.PlantsList> {
        PlantsListScreen(
            onAddPlant = {
                navController.navigate(Screen.AddPlant())
            }
        )
    }
}
