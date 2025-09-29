package com.jacob.budgetbowl

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


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



    }

    //GOT IT!!!
    private fun AddUserToDatabse()
    {
        val foundIntent = intent
        val userEmail = foundIntent.getStringExtra("UserEmail")
        //create a userDAO instance and then add it to room
        val userData = UserData(userName = "", NameSurname = "", Password = "", MinBudget = 0, MaxBudget = 10, TargetBudget = 5)
        userDAO.insertUser(userData)
    }
}