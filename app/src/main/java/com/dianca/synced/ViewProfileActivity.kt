package com.dianca.synced

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class ViewProfileActivity : AppCompatActivity() {

    private lateinit var imgProfile: ImageView
    private lateinit var txtNameAge: TextView
    private lateinit var txtGenderLocation: TextView
    private lateinit var txtBio: TextView
    private lateinit var btnSendMessage: Button

    private lateinit var db: FirebaseFirestore
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_profile)

        imgProfile = findViewById(R.id.imgProfile)
        txtNameAge = findViewById(R.id.txtNameAge)
        txtGenderLocation = findViewById(R.id.txtGenderLocation)
        txtBio = findViewById(R.id.txtBio)
        btnSendMessage = findViewById(R.id.btnSendMessage)

        db = FirebaseFirestore.getInstance()
        userId = intent.getStringExtra("uid")

        if (userId == null) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadUserProfile(userId!!)

        btnSendMessage.setOnClickListener {
            Toast.makeText(this, "Message request sent ✅", Toast.LENGTH_SHORT).show()
            // You can navigate to a real message request screen later
        }
    }

    private fun loadUserProfile(uid: String) {
        db.collection("users").document(uid).get().addOnSuccessListener { doc ->
            val name = doc.getString("name") ?: "Unknown"
            val age = doc.getLong("age")?.toInt() ?: 0
            val gender = doc.getString("gender") ?: "N/A"
            val location = doc.getString("location") ?: "N/A"
            val bio = doc.getString("bio") ?: "No bio provided"
            val imageUrl = doc.getString("profileImageUrl") ?: ""

            txtNameAge.text = "$name, $age"
            txtGenderLocation.text = "$gender - $location"
            txtBio.text = bio

            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.default_avatar_foreground)
                .into(imgProfile)
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show()
        }
    }
}
