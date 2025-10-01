package com.jacob.budgetbowl

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


//Ref module manual for coroutine

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

    private lateinit var roomDB: AppDatabase

    private lateinit var expenseDAO: ExpenseDAO



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

        //get Database and DAO
        roomDB = AppDatabase.getDatabase(this) as AppDatabase
        expenseDAO = roomDB.expenseDAO()


        btnConfrim.setOnClickListener {
            addExpenseEntry()
            //start next activity
        }


    }


    private fun addExpenseEntry()
    {
        //validate data
        //create expense object and add it to db
        // figure out how to optionally add a picture to the expense


        if(etAmountSpentInput.text.isBlank())//validate rest of fields
        {
            Toast.makeText(this,"all fields need to be filled", Toast.LENGTH_SHORT).show()
        }

        val amountSpent = etAmountSpentInput.text.toString().toInt()
        val dateAddedString = etDateInput.text.toString()
        val dateFormat = SimpleDateFormat("dd/mm/yyyy", Locale.getDefault())
        val dateObject =dateFormat.parse(dateAddedString)
        val description = etDescriptionInput.text.toString()


        val expenseData = ExpenseEntry(
            ExpenseAmount = amountSpent,
            ExpenseDate = dateObject!!,
            CatId = ECategory.Groceries,
            ExpesnseDescription = description
        )

        CoroutineScope(Dispatchers.IO).launch {expenseDAO.insertExpense(expenseData)}


    }
}