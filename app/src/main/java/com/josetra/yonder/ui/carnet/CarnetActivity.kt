package com.josetra.yonder.ui.carnet

import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.josetra.yonder.R

class CarnetActivity : AppCompatActivity() {

    private lateinit var profileImage: ImageView
    private lateinit var nameText: TextView
    private lateinit var universityText: TextView
    private lateinit var careerText: TextView
    private lateinit var carnetRoot: androidx.cardview.widget.CardView

    private lateinit var auth: FirebaseAuth
    private lateinit var db: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carnet)

        profileImage = findViewById(R.id.universityLogoImageView)
        nameText = findViewById(R.id.nameTextView)
        universityText = findViewById(R.id.universityTextView)
        careerText = findViewById(R.id.careerTextView)
        carnetRoot = findViewById(R.id.carnetCardView)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance().reference

        val uid = auth.currentUser?.uid
        if (uid == null) {
            finish()
            return
        }

        db.child("users").child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("fullName").getValue(String::class.java) ?: ""
                val universityId = snapshot.child("university").getValue(String::class.java) ?: ""
                val career = snapshot.child("major").getValue(String::class.java) ?: ""

                nameText.text = name
                careerText.text = career

                // Cargar datos de la universidad
                db.child("universidades").child(universityId)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(univSnap: DataSnapshot) {
                            val universityName = univSnap.child("nombre").getValue(String::class.java) ?: ""
                            val logoUrl = univSnap.child("urlInsignia").getValue(String::class.java) ?: ""
                            val colorPrimario = univSnap.child("colorPrimario").getValue(String::class.java) ?: "#2196F3"
                            universityText.text = universityName

                            // Cambiar color de fondo del carnet
                            carnetRoot.setCardBackgroundColor(Color.parseColor(colorPrimario))

                            // Cargar insignia desde internet
                            Glide.with(this@CarnetActivity)
                                .load(logoUrl)
                                .placeholder(R.drawable.default_logo)
                                .into(profileImage)
                        }
                        override fun onCancelled(error: DatabaseError) {}
                    })
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
