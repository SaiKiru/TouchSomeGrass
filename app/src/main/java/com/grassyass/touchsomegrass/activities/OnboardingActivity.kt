package com.grassyass.touchsomegrass.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.viewpager2.widget.ViewPager2
import com.grassyass.touchsomegrass.R
import com.grassyass.touchsomegrass.adapters.OnboardingPagerAdapter
import com.grassyass.touchsomegrass.data.local.Initialization
import com.grassyass.touchsomegrass.data.models.OnboardingData

class OnboardingActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var readyButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        Initialization.application = application

        if (Initialization.isAppInitialized()) {
            Log.i("grassy onboard", "not your first time ehh..")
            navigateToSignUp()
        } else {
            Log.i("grassy onboard", "first time, loading pages...")
            loadOnboardingScreens()
        }
    }

    private fun loadOnboardingScreens() {
        viewPager = findViewById(R.id.view_pager)
        readyButton = findViewById(R.id.ready_button)

        val onboardingData = listOf(
            OnboardingData(
                "Welcome, potato!",
                "Do you ever find yourself spending too much time on your phone, or on your couch?",
                getDrawable(R.drawable.illus_man_on_couch)
            ),
            OnboardingData(
                "Let's touch some grass!",
                "We'll temporarily lock your phone so you can get up and enjoy life for a bit",
                getDrawable(R.drawable.illus_locked_phone)
            ),
            OnboardingData(
                "But don't worry!",
                "You'll just need to walk for a few minutes to use your phone again",
                getDrawable(R.drawable.illus_walking_man)
            ),
            OnboardingData(
                "Are you ready to start your fitness journey?",
                "",
                getDrawable(R.drawable.illus_running_man)
            )
        )

        viewPager.adapter = OnboardingPagerAdapter(this, onboardingData)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                if (position == onboardingData.size - 1) {
                    readyButton.visibility = View.VISIBLE
                } else {
                    readyButton.visibility = View.INVISIBLE
                }
            }
        })

        readyButton.setOnClickListener {
            Log.i("grassy onboard", "initializing app...")
            Initialization.setAppAsInitialized()
            navigateToSignUp()
        }
    }

    private fun navigateToSignUp() {
        Intent(this, SignUpActivity::class.java).also { intent ->
            startActivity(intent)
        }
    }
}