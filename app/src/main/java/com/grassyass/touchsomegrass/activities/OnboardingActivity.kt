package com.grassyass.touchsomegrass.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        Initialization.application = application

        if (Initialization.isAppInitialized()) {
            navigateToLogin()
        } else {
            setContentView(R.layout.activity_onboarding)
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
                    readyButton.text = "I'm Ready!"
                } else {
                    readyButton.text = "Next"
                }
            }
        })

        readyButton.setOnClickListener {
            if (viewPager.currentItem == onboardingData.size - 1) {
                Initialization.setAppAsInitialized()
                navigateToSignUp()
            } else {
                viewPager.setCurrentItem(viewPager.currentItem + 1, true)
            }
        }
    }

    private fun navigateToSignUp() {
        Intent(this, SignUpActivity::class.java).also { intent ->
            startActivity(intent)
        }
    }

    private fun navigateToLogin() {
        Intent(this, LoginActivity::class.java).also { intent ->
            startActivity(intent)
        }
    }
}