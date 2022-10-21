package com.grassyass.touchsomegrass.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.grassyass.touchsomegrass.R
import com.grassyass.touchsomegrass.fragments.HomeFragment
import com.grassyass.touchsomegrass.fragments.ProfileFragment
import com.grassyass.touchsomegrass.fragments.ExercisesFragment

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var homeScreen: Fragment
    private lateinit var exercisesScreen: Fragment
    private lateinit var profileScreen: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeUI()
    }

    override fun onResume() {
        super.onResume()

        when(bottomNavigation.selectedItemId) {
            R.id.nav_item_exercises_screen -> { changeScreen(exercisesScreen) }
            R.id.nav_item_profile_screen -> { changeScreen(profileScreen) }
            else -> { changeScreen(homeScreen) }
        }
    }

    private fun initializeUI() {
        bottomNavigation = findViewById(R.id.bottom_navigation)

        homeScreen = HomeFragment()
        exercisesScreen = ExercisesFragment()
        profileScreen = ProfileFragment()

        bottomNavigation.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.nav_item_home_screen -> { changeScreen(homeScreen) }
                R.id.nav_item_exercises_screen -> { changeScreen(exercisesScreen) }
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