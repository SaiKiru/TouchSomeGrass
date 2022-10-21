package com.grassyass.touchsomegrass.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.FirebaseUser
import com.grassyass.touchsomegrass.R
import com.grassyass.touchsomegrass.network.Authentication

class LoginActivity : AppCompatActivity() {
    private lateinit var emailInputField: EditText
    private lateinit var passwordInputField: EditText
    private lateinit var loginButton: Button
    private lateinit var signUpButton: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailInputField = findViewById(R.id.email_input_field)
        passwordInputField = findViewById(R.id.password_input_field)
        loginButton = findViewById(R.id.login_button)
        signUpButton = findViewById(R.id.sign_up_button)

        loginButton.setOnClickListener { onLoginButtonPressed() }
        signUpButton.setOnClickListener { onSignUpButtonPressed() }

        val spannable = SpannableString(signUpButton.text)
        spannable.setSpan(UnderlineSpan(), 0, spannable.length, 0)
        signUpButton.text = spannable
    }

    override fun onStart() {
        super.onStart()

        val currentUser: FirebaseUser? = Authentication.getCurrentUser()
        if (currentUser != null) { navigateToHome() }
    }

    private fun onLoginButtonPressed() {
        val email: String = emailInputField.text.toString()
        val password: String = passwordInputField.text.toString()

        Authentication.signIn(email, password, { navigateToHome() })
    }

    private fun onSignUpButtonPressed() {
        navigateToSignUpScreen()
    }

    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToSignUpScreen() {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }
}