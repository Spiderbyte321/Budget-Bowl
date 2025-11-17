package com.jacob.budgetbowl

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class PrizeTank : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var prizeAdapter: PrizeAdapter
    private val prizeUrls = mutableListOf<String>()
    private lateinit var databaseReference: DatabaseReference
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
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid
            databaseReference = FirebaseDatabase.getInstance().reference.child("users").child(userId).child("prizes")

            fetchPrizes()
        } else {
            // Handle user not logged in case
        }
    }

    private fun fetchPrizes() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                prizeUrls.clear()
                for (prizeSnapshot in snapshot.children) {
                    val prizeUrl = prizeSnapshot.getValue(String::class.java)
                    prizeUrl?.let { prizeUrls.add(it) }
                }

                if (prizeUrls.isEmpty()) {
                    showNoPrizesDialog()
                } else {
                    prizeAdapter = PrizeAdapter(prizeUrls)
                    recyclerView.adapter = prizeAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
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