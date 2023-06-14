package com.alberto.recipegram

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_home -> {
                    navController.navigate(R.id.homeFragment)
                    true
                }
                R.id.action_profile -> {
                    navController.navigate(R.id.profileFragment)
                    true
                }
                 R.id.action_search -> {
                    navController.navigate(R.id.searchFragment)
                    true
                }
                R.id.action_upload -> {
                    navController.navigate(R.id.uploadFragment)
                    true
                }
                else -> false
            }
        }
    }
}