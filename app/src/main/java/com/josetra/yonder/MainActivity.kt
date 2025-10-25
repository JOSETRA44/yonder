package com.josetra.yonder

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.josetra.yonder.databinding.ActivityMainBinding
import com.josetra.yonder.ui.login.LoginActivity

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        binding.welcomeTextView.text = "Bienvenido, ${currentUser.email}"

        binding.carnetButton.setOnClickListener {
            startActivity(Intent(this, com.josetra.yonder.ui.carnet.CarnetActivity::class.java))
        }

        binding.logoutButton.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, com.josetra.yonder.ui.auth.AuthActivity::class.java))
            finish()
        }
    }
}
