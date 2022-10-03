package com.grassyass.touchsomegrass.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.grassyass.touchsomegrass.R
import com.grassyass.touchsomegrass.fragments.HomeFragment
import com.grassyass.touchsomegrass.fragments.ProfileFragment
import com.grassyass.touchsomegrass.fragments.WorkoutsFragment

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var homeScreen: Fragment
    private lateinit var workoutsScreen: Fragment
    private lateinit var profileScreen: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeUI()
    }

    override fun onStart() {
        super.onStart()

        changeScreen(homeScreen)
    }

    private fun initializeUI() {
        bottomNavigation = findViewById(R.id.bottom_navigation)

        homeScreen = HomeFragment()
        workoutsScreen = WorkoutsFragment()
        profileScreen = ProfileFragment()

        bottomNavigation.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.nav_item_home_screen -> { changeScreen(homeScreen) }
                R.id.nav_item_workouts_screen -> { changeScreen(workoutsScreen) }
                R.id.nav_item_profile_screen -> { changeScreen(profileScreen) }
                else -> false
            }
        }
    }

    private fun changeScreen(screen: Fragment): Boolean {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_switcher, screen)
            commit()
        }

        return true
    }
}