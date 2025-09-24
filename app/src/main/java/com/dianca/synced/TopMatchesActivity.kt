package com.dianca.synced

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dianca.synced.adapters.MatchAdapter
import com.dianca.synced.models.MatchModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TopMatchesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MatchAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var spinnerGender: Spinner
    private lateinit var etLocation: EditText
    private lateinit var btnRefresh: Button

    private var allMatches = mutableListOf<MatchModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top_matches)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        recyclerView = findViewById(R.id.recyclerMatches)
        progressBar = findViewById(R.id.progressBar)
        spinnerGender = findViewById(R.id.spinnerGender)
        etLocation = findViewById(R.id.etLocation)
        btnRefresh = findViewById(R.id.btnRefresh)

        adapter = MatchAdapter(allMatches) { selectedMatch ->
            val intent = Intent(this, ViewProfileActivity::class.java)
            intent.putExtra("uid", selectedMatch.uid)
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        spinnerGender.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item,
            listOf("Any", "Male", "Female")
        )

        loadMatches()

        btnRefresh.setOnClickListener {
            loadMatches()
        }
    }

    private fun loadMatches() {
        progressBar.visibility = View.VISIBLE
        val currentUser = auth.currentUser ?: return
        val genderFilter = spinnerGender.selectedItem.toString()
        val locationInput = etLocation.text.toString().trim()

        db.collection("users").get().addOnSuccessListener { result ->
            allMatches.clear()

            for (doc in result) {
                val uid = doc.id
                if (uid == currentUser.uid) continue // Skip self

                val name = doc.getString("name") ?: ""
                val age = doc.getLong("age")?.toInt() ?: 0
                val bio = doc.getString("bio") ?: "No bio provided"
                val gender = doc.getString("gender") ?: ""
                val location = doc.getString("location") ?: ""
                val imageUrl = doc.getString("profileImageUrl") ?: ""

                if ((genderFilter == "Any" || gender == genderFilter) &&
                    (locationInput.isEmpty() || location.contains(locationInput, ignoreCase = true))) {

                    val match = MatchModel(uid, name, age, bio, gender, location, imageUrl)
                    allMatches.add(match)
                }
            }

            adapter.notifyDataSetChanged()
            progressBar.visibility = View.GONE
        }
    }
}
