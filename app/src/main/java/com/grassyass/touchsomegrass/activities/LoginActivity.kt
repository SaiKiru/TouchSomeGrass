package com.grassyass.touchsomegrass.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseUser
import com.grassyass.touchsomegrass.R
import com.grassyass.touchsomegrass.data.network.Authentication

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

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Fields must not be empty", Toast.LENGTH_SHORT).show()
            return
        }

        if (!email.matches(Regex("[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}"))) {
            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show()
            return
        }

        Authentication.signIn(email, password, {
            navigateToHome()
        }, {
            Toast.makeText(this, "Could not sign in. Check your email and password, or try again later", Toast.LENGTH_LONG).show()
        })
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