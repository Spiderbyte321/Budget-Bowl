package com.jacob.budgetbowl

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID


////https://developer.android.com/topic/libraries/architecture/coroutines
//For the coroutine
//Same reference for the spinner population
//Reference Module Manual for camera perms

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

    private var optionalImage: Bitmap? = null

    private lateinit var spinnerAdapter: ArrayAdapter<ECategory>

    private val db = FirebaseFirestore.getInstance()

    private val userID = FirebaseAuth.getInstance().currentUser?.uid




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
        val btnBack : Button = findViewById(R.id.backBtn)

        btnBack.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }


        //get Database and DAO
        roomDB = AppDatabase.getDatabase(this) as AppDatabase
        expenseDAO = roomDB.expenseDAO()

        //get camera perms and take the photo back from the camera and assign to variable

        val cameraProviderResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                var bitmap = it.data!!.extras?.get("data") as Bitmap
                optionalImage=bitmap
            }
        }//(Moodley,2025)

        //ad all our elements to our spinner
        spinnerAdapter = ArrayAdapter<ECategory>(
            this,
            android.R.layout.simple_spinner_item,
            ECategory.entries
        )//(Google,S.A)

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)//(Google,S.A)
        spCategoriesInput.adapter = spinnerAdapter//(Google,S.A)



        btnAddImage.setOnClickListener() {
            var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraProviderResult.launch(intent)
        }




        btnConfrim.setOnClickListener {
            addExpenseEntry()
        }


    }


    private fun addExpenseEntry()
    {
        //validate data
        //create expense object and add it to db
        // figure out how to optionally add a picture to the expense


        if(etAmountSpentInput.text.isBlank()||etDateInput.text.isBlank()||etDescriptionInput.text.isBlank())//validate rest of fields
        {
            Toast.makeText(this,"all fields need to be filled", Toast.LENGTH_SHORT).show()
            return
        }

        val amountSpent = etAmountSpentInput.text.toString().toInt()
        val dateAdded = etDateInput.text.toString()
        val description = etDescriptionInput.text.toString()

        val dateFormatter = SimpleDateFormat("dd-mm-yyyy", Locale.getDefault())


        try {

            val startObject =dateFormatter.parse(dateAdded)


        }catch (e: ParseException)
        {
            Toast.makeText(this,"Date formatted incorrectly must be : dd-mm-yyyy",Toast.LENGTH_SHORT).show()
            return
        }


        val expenseData = ExpenseObject(//before creating the object push it to the database and get the link to this object
            ExpenseAmount = amountSpent,
            ExpenseDate = dateAdded,
            CatId = "Mortgage",
            ExpesnseDescription = description,
        )

        val expenseMap = mutableMapOf<String, ExpenseObject>(
            UUID.randomUUID().toString() to expenseData
        )

        userID?.let {
            db.collection(userID).document(expenseData.CatId)
                .collection("expenses").document(expenseData.ExpenseDate)
                .set(expenseData)
                .addOnSuccessListener {
                    Toast.makeText(this,"Expense Added", Toast.LENGTH_SHORT).show()
                }
        }

       userID?.let {
            db.collection(userID).document(expenseData.CatId).get().addOnSuccessListener {
                results->
                val  category: CategoryObject? = results.toObject(CategoryObject::class.java)
                category?.categoryTotalExpenditure +=expenseData.ExpenseAmount

                val databaseCat = mutableMapOf<String, Any>("totalexpenditure" to category?.categoryTotalExpenditure.toString())
                db.collection(userID).document(expenseData.CatId).set(databaseCat)
            }
        }


        //move this toa back button later but works for now
        val intent =Intent(this, DashboardActivity::class.java)
        Toast.makeText(this,"Successfully added expense", Toast.LENGTH_SHORT).show()

        startActivity(intent)
    }

    /// References:
    // Android Open Source Project. 10 February 2025.Use Kotlin coroutines with lifecycle-aware components.[Online] Avaliable at: https://developer.android.com/topic/libraries/architecture/coroutines [Accessed on: 1 October 2025]
    //// The Independent Institute of Education. 2025. Open Source Coding Module Manuel  [OPSC 7311]. nt. [online via internal VLE] The Independent Institute of Education. Available at: https://advtechonline.sharepoint.com/:w:/r/sites/TertiaryStudents/_layouts/15/Doc.aspx?sourcedoc=%7BD5C243B5-895D-4B63-B083-140930EF9734%7D&file=OPSC7311MM.docx&action=default&mobileredirect=true [Accessed on: 30 September 2025].
}