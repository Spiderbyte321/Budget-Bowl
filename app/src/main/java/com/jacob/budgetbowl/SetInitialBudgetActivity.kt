package com.jacob.budgetbowl

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
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


//Reference Module Manual
class SetInitialBudgetActivity : AppCompatActivity() {
    //Take in values and which category then push it to room
    //uuuuuugggggghh
    //bind and set listeners oncreate
    //onbutton press push to room
    //for now gonna work on the room stuff
    //maybe use a share intent to get the user info to here and then push it onto out user table?
    //nope intent is in between apps we need to share between classes maybe through a constructor?
    // fetch user info and add it to the db
    //yea I hate working with android studio
    //Ok so now I get the db and add the user to it

    private lateinit var roomDB: AppDatabase
    private lateinit var userDAO: UserDAO

    private lateinit var inputMinBudget: EditText

    private lateinit var inputMaxBudget: EditText

    private lateinit var inputTargetBudget: EditText

    private lateinit var confirmButton: Button

    private lateinit var cancelButton: Button

    private lateinit var categorySpinner: Spinner

    private lateinit var adapter: ArrayAdapter<ECategory>



    //Spinner population
    //https://developer.android.com/develop/ui/views/components/spinner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_set_initial_budget)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        // ooohh ok we have to specify as appdatabase cause we want to return any type of database
        // and then cast it to our specific database
        //should get some food

         roomDB = AppDatabase.getDatabase(this) as AppDatabase
         userDAO = roomDB.userDAO()

        inputMinBudget = findViewById(R.id.minBudgetET)
        inputMaxBudget = findViewById(R.id.maxBudgetET)
        inputTargetBudget = findViewById(R.id.targerBudgetET)
        confirmButton = findViewById(R.id.confirmButton)
        cancelButton = findViewById(R.id.cancelButton)
        categorySpinner= findViewById(R.id.spCategoryDropDown)

        adapter = ArrayAdapter<ECategory>(
            this,
            android.R.layout.simple_spinner_item,
            ECategory.entries
        )//(Google,S.A)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)//(Google,S.A)
        categorySpinner.adapter = adapter//(Google,S.A)

        //set up category stuff
        //add all our enums to the spinner
        //This should now show each of our enums in the spinner


        confirmButton.setOnClickListener {
            AddUserToDatabse()
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }
        cancelButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


    }

    //GOT IT!!!
    private fun AddUserToDatabse()
    {

        //add value validation
        //also do the pop up to confirm

        val foundIntent = intent
        val userName= foundIntent.getStringExtra("UserName")
        val fullName= foundIntent.getStringExtra("FullName")
        val passWord = foundIntent.getStringExtra("UserPassword")
        val minBudget:Int = inputMinBudget.text.toString().toInt()
        val maxBudget:Int = inputMaxBudget.text.toString().toInt()
        val targetBudget:Int = inputTargetBudget.text.toString().toInt()
        //create a userDAO instance and then add it to room
        //!! forces the value to be non-nullable
        //get extra returns String? which is a nullable string
        //while String is a non-nullable string data type
        //since we ensure the user fills in their details we can comfortably assume
        //that we don't need to feed default values or worry about null values
        val userData = UserData(UserName = userName!!, NameSurname = fullName!!, Password = passWord!!, MinBudget = minBudget, MaxBudget = maxBudget, TargetBudget = targetBudget)
        CoroutineScope(Dispatchers.IO).launch {userDAO.insertUser(userData)}

    }


    //Now i need to do the rest uuuuuuuggghhhh
    //somethings up with the insert
    //ok so we need to use a coroutine whenver we access the roomDB

    //References
    ////Android Open Source Project. 10 February 2025.Add spinners to your app .[Online] Avaliable at: https://developer.android.com/develop/ui/views/components/spinner [ Accessed on: 2 October 2025]
}