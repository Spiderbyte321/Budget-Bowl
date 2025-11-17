package com.jacob.budgetbowl.ui

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.jacob.budgetbowl.BaseActivity
import com.jacob.budgetbowl.DashboardActivity
import com.jacob.budgetbowl.R
import kotlin.random.Random

class PrizeScreen : BaseActivity() {

    private lateinit var pointsProgressBar: ProgressBar
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var fishButton: Button
    private lateinit var decorButton: Button
    private lateinit var homeButton: ImageView

    private val prizeCost = 100
    private lateinit var imgFish: ImageView
    private lateinit var imgDecor: ImageView
    private lateinit var imgCoinIcon: ImageView

    private val fishPrizes = listOf(
        R.drawable.prizefish1,
        R.drawable.prizefish2,
        R.drawable.prizefish3,
        R.drawable.prizefish4,
        R.drawable.prizefish5,
        R.drawable.prizefish6,
        R.drawable.prizefish7,
        R.drawable.prizefish8,
        R.drawable.prizefish9,
        R.drawable.prizefish10
    )

    private val decorPrizes = listOf(
        R.drawable.prizecoral,
        R.drawable.prizecoralrock,
        R.drawable.prizerock1,
        R.drawable.prizerock2,
        R.drawable.prizerock3
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_prize_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        pointsProgressBar = findViewById(R.id.pointsProgressBar)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        fishButton = findViewById(R.id.fishButton)
        decorButton = findViewById(R.id.decorButton)
        homeButton = findViewById(R.id.homeButton)

        imgDecor = findViewById(R.id.decorImage)
        imgFish = findViewById(R.id.fishImage)
        imgCoinIcon = findViewById(R.id.CoinIconIMG)

        imgFish.setImageResource(R.drawable.fish4)
        imgDecor.setImageResource(R.drawable.decoricon)
        imgCoinIcon.setImageResource(R.drawable.coinicon)

        loadUserPoints()

        fishButton.setOnClickListener {
            redeemPrize(fishPrizes)
        }

        decorButton.setOnClickListener {
            redeemPrize(decorPrizes)
        }

        homeButton.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadUserPoints() {
        val user = auth.currentUser
        if (user != null) {
            val uid = user.uid
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val points = document.getLong("points")?.toInt() ?: 0
                        pointsProgressBar.progress = points
                    } else {
                        Toast.makeText(this, "User profile not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to load points: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun redeemPrize(prizeList: List<Int>) {
        val user = auth.currentUser
        if (user != null) {
            val uid = user.uid
            val userRef = db.collection("users").document(uid)
            userRef.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val points = document.getLong("points")?.toInt() ?: 0
                    if (points >= prizeCost) {
                        userRef.update("points", FieldValue.increment(-prizeCost.toLong()))

                        val randomPrize = prizeList[Random.nextInt(prizeList.size)]
                        showPrizeDialog(randomPrize)
                        loadUserPoints()

                    } else {
                        Toast.makeText(this, "You do not have enough points", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun showPrizeDialog(prizeResId: Int) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_prize)

        val prizeImageView = dialog.findViewById<ImageView>(R.id.prizeImageView)
        val acceptButton = dialog.findViewById<Button>(R.id.acceptButton)

        prizeImageView.setImageResource(prizeResId)

        acceptButton.setOnClickListener {
            savePrizeToDatabase(prizeResId)
            dialog.dismiss()
        }

        val window = dialog.window
        if (window != null) {
            val displayMetrics = resources.displayMetrics
            val dialogHeight = (displayMetrics.heightPixels * 0.5).toInt()
            window.setLayout(displayMetrics.widthPixels, dialogHeight)
        }

        dialog.show()
    }

    private fun savePrizeToDatabase(prizeResId: Int) {
        val user = auth.currentUser
        if (user != null) {
            val uid = user.uid
            val prizeData = hashMapOf(
                "prizeResId" to prizeResId,
                "timestamp" to FieldValue.serverTimestamp()
            )
            db.collection("users").document(uid).collection("prizes").add(prizeData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Prize redeemed!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to save prize: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}