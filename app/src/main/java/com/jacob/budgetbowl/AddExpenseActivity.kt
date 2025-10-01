package com.jacob.budgetbowl

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AddExpenseActivity : AppCompatActivity() {
    //get references to all our stuff
    //take in their values
    //add it to room as an expense entry
    private lateinit var etAmountSpentInput: EditText

    private lateinit var etDateInput : EditText

    private lateinit var spCategoriesInput: Spinner

    private lateinit var etDescriptionInput: EditText

    private lateinit var btnConfrim: Button

    private lateinit var btnAddImage: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_expense)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Bind Inputs
        etAmountSpentInput = findViewById(R.id.etAmountSpent)
        etDateInput = findViewById(R.id.etEntryDate)
        spCategoriesInput = findViewById(R.id.spCategories)
        etDescriptionInput = findViewById(R.id.etDescription)
        btnConfrim = findViewById(R.id.btnAddEntry)
        btnAddImage = findViewById(R.id.btnAddImage)


        btnConfrim.setOnClickListener {
            addExpenseEntry()
            //start next activity
        }


    }


    private fun addExpenseEntry()
    {

    }
}