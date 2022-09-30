package com.grassyass.touchsomegrass.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseUser
import com.grassyass.touchsomegrass.R
import com.grassyass.touchsomegrass.network.Authentication

class LoginActivity : AppCompatActivity() {
    private lateinit var emailInputField: EditText
    private lateinit var passwordInputField: EditText
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initializeUI()
    }

    override fun onStart() {
        super.onStart()

        val currentUser: FirebaseUser? = Authentication.getCurrentUser()
        if (currentUser != null) { navigateToHome() }
    }

    private fun initializeUI() {
        emailInputField = findViewById(R.id.email_input_field)
        passwordInputField = findViewById(R.id.password_input_field)
        loginButton = findViewById(R.id.login_button)

        loginButton.setOnClickListener { onLoginButtonPressed() }
    }

    private fun onLoginButtonPressed() {
        val email: String = emailInputField.text.toString()
        val password: String = passwordInputField.text.toString()

        Authentication.signIn(email, password, { navigateToHome() })
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }
}