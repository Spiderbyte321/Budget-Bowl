package com.jacob.budgetbowl

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class activity_profile : AppCompatActivity() {

    private lateinit var etProfileFullname: EditText
    private lateinit var etProfileUsername: EditText
    private lateinit var btnSaveChanges: Button
    private lateinit var btnSetBudget: Button
    private lateinit var btnDiscardChanges: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etProfileFullname = findViewById(R.id.etProfileFullname)
        etProfileUsername = findViewById(R.id.etProfileUsername)
        btnSaveChanges = findViewById(R.id.btnSaveChanges)
        btnSetBudget = findViewById(R.id.btnSetBudget)
        btnDiscardChanges = findViewById(R.id.btnDiscardChanges)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        loadUserProfile()

        btnSetBudget.setOnClickListener {
            val intent = Intent(this, SetInitialBudgetActivity::class.java)
            startActivity(intent)
        }

        btnSaveChanges.setOnClickListener {
            saveUserProfile()
        }

        btnDiscardChanges.setOnClickListener {
            loadUserProfile()
            Toast.makeText(this, "Changes have been reverted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadUserProfile() {
        val user = auth.currentUser
        if (user != null) {
            val uid = user.uid
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val fullName = document.getString("fullName")
                        val userName = document.getString("userName")

                        etProfileFullname.setText(fullName)
                        etProfileUsername.setText(userName)
                    } else {
                        Toast.makeText(this, "User profile not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to load profile: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun saveUserProfile() {
        val user = auth.currentUser
        if (user != null) {
            val uid = user.uid
            val fullName = etProfileFullname.text.toString().trim()
            val userName = etProfileUsername.text.toString().trim()

            if (fullName.isNotEmpty() && userName.isNotEmpty()) {
                val userProfile = mapOf(
                    "fullName" to fullName,
                    "userName" to userName
                )

                db.collection("users").document(uid).update(userProfile)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to update profile: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            } else {
                Toast.makeText(this, "Full name and username cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }
}