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
import com.grassyass.touchsomegrass.R
import com.grassyass.touchsomegrass.data.models.User
import com.grassyass.touchsomegrass.data.network.Authentication
import com.grassyass.touchsomegrass.data.network.api.UsersAPI

class SignUpActivity : AppCompatActivity() {
    private lateinit var nameInputField: EditText
    private lateinit var emailInputField: EditText
    private lateinit var passwordInputField: EditText
    private lateinit var confirmPasswordInputField: EditText
    private lateinit var signUpButton: Button
    private lateinit var loginButton: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        nameInputField = findViewById(R.id.name_input_field)
        emailInputField = findViewById(R.id.email_input_field)
        passwordInputField = findViewById(R.id.password_input_field)
        confirmPasswordInputField = findViewById(R.id.confirm_password_input_field)
        signUpButton = findViewById(R.id.sign_up_button)
        loginButton = findViewById(R.id.login_button)

        signUpButton.setOnClickListener { onSignUpButtonPressed() }
        loginButton.setOnClickListener { onLoginButtonPressed() }

        val spannable = SpannableString(loginButton.text)
        spannable.setSpan(UnderlineSpan(), 0, spannable.length, 0)
        loginButton.text = spannable
    }

    private fun onLoginButtonPressed() {
        navigateToLoginScreen()
    }

    private fun onSignUpButtonPressed() {
        val username = nameInputField.text.toString()
        val email = emailInputField.text.toString()
        val password = passwordInputField.text.toString()
        val confirmPassword = confirmPasswordInputField.text.toString()

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Fields must not be empty", Toast.LENGTH_SHORT).show()
            return
        }

        if (!email.matches(Regex("[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}"))) {
            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 8) {
            Toast.makeText(this, "Password is too short. It should be at least 8 characters in length", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Your passwords are not the same", Toast.LENGTH_SHORT).show()
            return
        }

        Authentication.createAccount(email, password, {
            val user = User(username, 0.0)
            UsersAPI.addUser(user)

            navigateToHome()
        }, {
            Toast.makeText(this, "Could not connect to server. Try again later", Toast.LENGTH_SHORT).show()
        })
    }

    private fun navigateToHome() {
        Intent(this, MainActivity::class.java).also { intent ->
            startActivity(intent)
        }
        finish()
    }

    private fun navigateToLoginScreen() {
        Intent(this, LoginActivity::class.java).also { intent ->
            startActivity(intent)
        }
    }
}