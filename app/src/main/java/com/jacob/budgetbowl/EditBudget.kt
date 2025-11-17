package com.jacob.budgetbowl

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditBudget : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    private lateinit var inputMinBudget: EditText
    private lateinit var inputMaxBudget: EditText
    private lateinit var inputTargetBudget: EditText
    private lateinit var confirmButton: Button
    private lateinit var cancelButton: Button

    private lateinit var questionMarkMaxBudget: ImageView
    private lateinit var questionMarkMinBudget: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_budget)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        inputMinBudget = findViewById(R.id.minBudgetET)
        inputMaxBudget = findViewById(R.id.maxBudgetET)
        inputTargetBudget = findViewById(R.id.targetBudgetET)
        confirmButton = findViewById(R.id.confirmButton)
        cancelButton = findViewById(R.id.cancelButton)
        questionMarkMaxBudget = findViewById(R.id.questionMarkMaxBudget)
        questionMarkMinBudget = findViewById(R.id.questionMarkMinBudget)

        loadBudgetData()

        confirmButton.setOnClickListener {
            saveOverallBudget()
            navigateToHome()
        }

        cancelButton.setOnClickListener {
            navigateToHome()
        }

        questionMarkMaxBudget.setOnClickListener {
            showInfoDialog("Maximum Budget", "This is the maximum amount you can extend over your budget. It must be higher than your target.")
        }

        questionMarkMinBudget.setOnClickListener {
            showInfoDialog("Minimum Budget", "This is the minimum amount you can spend in a month. It must be lower than your target. .")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                navigateToHome()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun loadBudgetData() {
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val minBudget = document.getLong("minBudget")?.toString()
                        val maxBudget = document.getLong("maxBudget")?.toString()
                        val targetBudget = document.getLong("targetBudget")?.toString()

                        inputMinBudget.setText(minBudget)
                        inputMaxBudget.setText(maxBudget)
                        inputTargetBudget.setText(targetBudget)
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error loading budget data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun saveOverallBudget() {
        val minBudget = inputMinBudget.text.toString().toIntOrNull() ?: 0
        val maxBudget = inputMaxBudget.text.toString().toIntOrNull() ?: 0
        val targetBudget = inputTargetBudget.text.toString().toIntOrNull() ?: 0

        if (userId != null) {
            val userUpdates = mapOf(
                "minBudget" to minBudget,
                "maxBudget" to maxBudget,
                "targetBudget" to targetBudget
            )
            db.collection("users").document(userId).update(userUpdates)
                .addOnSuccessListener {
                    Toast.makeText(this, "Overall budget saved!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error saving overall budget: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun showInfoDialog(title: String, message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK", null)
        builder.show()
    }

}
