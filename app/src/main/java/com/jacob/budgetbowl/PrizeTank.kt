package com.jacob.budgetbowl

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PrizeTank : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var prizeAdapter: PrizeAdapter
    private val prizeNames = mutableListOf<String>()
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prize_tank)

        val homeButton: ImageView = findViewById(R.id.homeButton)
        homeButton.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }

        recyclerView = findViewById(R.id.Recycler)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid
            fetchPrizes(userId)
        } else {
            // Handle user not logged in case
        }
    }

    private fun fetchPrizes(userId: String) {
        db.collection("users").document(userId).collection("prizes")
            .orderBy("timestamp")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                prizeNames.clear()
                for (doc in snapshots!!) {
                    val prizeName = doc.getString("prizeName")
                    if (prizeName != null) {
                        prizeNames.add(prizeName)
                    }
                }

                if (prizeNames.isEmpty()) {
                    showNoPrizesDialog()
                } else {
                    prizeAdapter = PrizeAdapter(prizeNames)
                    recyclerView.adapter = prizeAdapter
                }
            }
    }

    private fun showNoPrizesDialog() {
        AlertDialog.Builder(this)
            .setTitle("No Prizes Yet!")
            .setMessage("You haven't won any prizes yet. Keep saving to unlock them!")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
                finish()
            }
            .setCancelable(false)
            .show()
    }
}