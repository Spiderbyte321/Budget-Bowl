package com.jacob.budgetbowl

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.jacob.budgetbowl.ui.ExpenseReview.DatePickerFragment
import java.io.ByteArrayOutputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID


class AddExpenseActivity : AppCompatActivity() {
    private lateinit var etAmountSpentInput: EditText
    private lateinit var etDateInput : EditText
    private lateinit var spCategoriesInput: Spinner
    private lateinit var etDescriptionInput: EditText
    private lateinit var btnConfrim: Button
    private lateinit var btnAddImage: Button
    private var optionalImage: Bitmap? = null
    private lateinit var spinnerAdapter: ArrayAdapter<ECategory>
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val userID = FirebaseAuth.getInstance().currentUser?.uid
    private lateinit var homeButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_expense)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportFragmentManager.setFragmentResultListener("entryDateRequestKey", this) { _, bundle ->
            val result = bundle.getString("selectedDate")
            etDateInput.setText(result)
        }

        etAmountSpentInput = findViewById(R.id.etAmountSpent)
        etDateInput = findViewById(R.id.etEntryDate)
        spCategoriesInput = findViewById(R.id.spCategories)
        etDescriptionInput = findViewById(R.id.etDescription)
        btnConfrim = findViewById(R.id.btnAddEntry)
        btnAddImage = findViewById(R.id.btnAddImage)
        val btnBack : Button = findViewById(R.id.backBtn)
        homeButton = findViewById(R.id.homeButton)

        homeButton.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }

        btnBack.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }

        etDateInput.setOnClickListener {
            val datePicker = DatePickerFragment.newInstance("entryDateRequestKey")
            datePicker.show(supportFragmentManager, "datePicker")
        }

        val cameraProviderResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                var bitmap = it.data!!.extras?.get("data") as Bitmap
                optionalImage=bitmap
            }
        }

        spinnerAdapter = ArrayAdapter<ECategory>(
            this,
            android.R.layout.simple_spinner_item,
            ECategory.entries
        )

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCategoriesInput.adapter = spinnerAdapter

        btnAddImage.setOnClickListener() {
            var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraProviderResult.launch(intent)
        }

        btnConfrim.setOnClickListener {
            addExpenseEntry()
        }
    }

    private fun addExpenseEntry() {
        if(etAmountSpentInput.text.isBlank()||etDateInput.text.isBlank()||etDescriptionInput.text.isBlank())
        {
            Toast.makeText(this,"all fields need to be filled", Toast.LENGTH_SHORT).show()
            return
        }

        val amountSpent = etAmountSpentInput.text.toString().toInt()
        val dateAdded = etDateInput.text.toString()
        val description = etDescriptionInput.text.toString()
        val category = spCategoriesInput.selectedItem.toString()
        val expenseId = UUID.randomUUID().toString()

        val expenseEntry = ExpenseEntry(
            id = expenseId,
            expenseAmount = amountSpent,
            expenseDate = dateAdded,
            category = category,
            expenseDescription = description
        )

        if (optionalImage != null) {
            val baos = ByteArrayOutputStream()
            optionalImage?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            val imageRef = storage.reference.child("expense_images/${expenseId}.jpg")

            imageRef.putBytes(data)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        val expenseWithImage = expenseEntry.copy(imageUrl = imageUrl)
                        saveExpenseToFirestore(expenseWithImage)
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to upload image: ${e.message}", Toast.LENGTH_LONG).show()
                }
        } else {
            saveExpenseToFirestore(expenseEntry)
        }
    }

    private fun saveExpenseToFirestore(expense: ExpenseEntry) {
        if (userID != null) {
            db.collection("users").document(userID).collection("expenses").document(expense.id)
                .set(expense)
                .addOnSuccessListener {
                    Toast.makeText(this,"Expense Added", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to add expense: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}
