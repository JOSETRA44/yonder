package com.josetra.yonder.ui.register

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.josetra.yonder.R
import com.josetra.yonder.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.josetra.yonder.databinding.ActivityRegisterBinding


class RegisterActivity : AppCompatActivity() {

    private lateinit var fullNameEditText: EditText
    private lateinit var universityEditText: EditText
    private lateinit var majorEditText: EditText
    private lateinit var universityCodeEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var goToLoginTextView: TextView

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        fullNameEditText = findViewById(R.id.fullNameEditText)
        universityEditText = findViewById(R.id.universityEditText)
        majorEditText = findViewById(R.id.majorEditText)
        universityCodeEditText = findViewById(R.id.universityCodeEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        registerButton = findViewById(R.id.registerButton)
        goToLoginTextView = findViewById(R.id.goToLoginTextView)

        registerButton.setOnClickListener {
            registerUser()
        }

        goToLoginTextView.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun registerUser() {
        val fullName = fullNameEditText.text.toString().trim()
        val university = universityEditText.text.toString().trim()
        val major = majorEditText.text.toString().trim()
        val universityCode = universityCodeEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString()
        val confirmPassword = confirmPasswordEditText.text.toString()

        if (fullName.isEmpty() || university.isEmpty() || major.isEmpty() ||
            universityCode.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()
        ) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                    val database = FirebaseDatabase.getInstance()
                    val userRef = database.getReference("users").child(userId)

                    val userData = mapOf(
                        "fullName" to fullName,
                        "university" to university,
                        "major" to major,
                        "universityCode" to universityCode,
                        "email" to email
                    )

                    userRef.setValue(userData).addOnCompleteListener { dataTask ->
                        if (dataTask.isSuccessful) {
                            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Error al guardar datos", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}
