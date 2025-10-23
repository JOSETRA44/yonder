package com.josetra.yonder.ui.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.FirebaseDatabase
import com.josetra.yonder.databinding.ActivityRegisterBinding
import com.josetra.yonder.model.User
import com.josetra.yonder.ui.login.LoginActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.registerButton.setOnClickListener {
            registerUser()
        }

        binding.goToLoginTextView.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun registerUser() {
        val fullName = binding.fullNameEditText.text.toString().trim()
        val university = binding.universityEditText.text.toString().trim()
        val major = binding.majorEditText.text.toString().trim()
        val universityCode = binding.universityCodeEditText.text.toString().trim()
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString()
        val confirmPassword = binding.confirmPasswordEditText.text.toString()

        if (!validateInput(fullName, university, major, universityCode, email, password, confirmPassword)) {
            return
        }

        showLoading(true)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        saveUserToDatabase(userId, fullName, university, major, universityCode, email)
                    } else {
                        showLoading(false)
                        Toast.makeText(this, "Error al obtener el ID del usuario.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    showLoading(false)
                    handleRegistrationFailure(task.exception)
                }
            }
    }

    private fun saveUserToDatabase(userId: String, fullName: String, university: String, major: String, universityCode: String, email: String) {
        val user = User(fullName, university, major, universityCode, email)
        database.getReference("users").child(userId).setValue(user)
            .addOnCompleteListener { dataTask ->
                showLoading(false)
                if (dataTask.isSuccessful) {
                    Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Error al guardar datos: ${dataTask.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun validateInput(vararg fields: String): Boolean {
        fields.forEach { field ->
            if (field.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
                return false
            }
        }

        if (fields[5] != fields[6]) { // Password and Confirm Password
            Toast.makeText(this, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun handleRegistrationFailure(exception: Exception?) {
        val errorMessage = when (exception) {
            is FirebaseAuthUserCollisionException -> "El correo electrónico ya está en uso."
            else -> "Error en el registro: ${exception?.message}"
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.registerButton.isEnabled = !isLoading
    }
}
