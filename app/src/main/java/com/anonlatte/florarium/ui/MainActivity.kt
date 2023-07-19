package com.anonlatte.florarium.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.anonlatte.florarium.R
import com.anonlatte.florarium.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private val appBarConfiguration by lazy { AppBarConfiguration(navController.graph) }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupNavigationController()
        setupNavigationBar()
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    private fun setupNavigationController() {
        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_fragment
        ) as NavHostFragment
        navController = navHostFragment.navController
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigation.isVisible = appBarConfiguration.topLevelDestinations.contains(
                destination.id
            )
        }
    }

    private fun setupNavigationBar() {
        binding.bottomNavigation.selectedItemId = R.id.nav_home
        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_plants -> {
                    // navController.navigate(R.id.plantsFragment)
                    false
                }

                R.id.nav_home -> {
                    navController.navigate(R.id.homeFragment)
                    true
                }

                R.id.nav_profile -> {
                    // navController.navigate(R.id.profileFragment)
                    false
                }

                else -> false
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
