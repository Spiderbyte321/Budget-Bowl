package com.jacob.budgetbowl.ui.ExpenseReview

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.jacob.budgetbowl.AppDatabase
import com.jacob.budgetbowl.ExpenseDAO
import com.jacob.budgetbowl.ExpenseEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExpenseView(application: Application): AndroidViewModel(application) {
    //Ok so in here is where the data for the user is stored
    //and define the logic for getting that data
    //ok so plan of action
    //Step 1 get all the user expenses
    //step 2 assign them to the variable in here
    //step 3 make the recycler view observe thevariable in here and update it's items
    //https://developer.android.com/topic/libraries/architecture/coroutines
    //Getting the user data in the view

    //Deffinitly reference chat
    //Prompt: No thank you rather I want to get some feedback on an idea I have for implementing a feature.
    // I have a recycler view on a fragment that I want to populate with information from a database.
    // I know that I need to user coroutine to fetch the data from the database which I found the KTX dependendancy
    // that allows me to fetch live user data and serve it to the fragment.
    // I'm thinking that I just have fragment observe the variable that will hold the livedata
    // and serve it to the recycler to display is this the correct approach to take?

    private val db = AppDatabase.getDatabase(application.applicationContext) as AppDatabase
    private val ExpenseDAO = db.expenseDAO()

    val UserExpenses: LiveData<List<ExpenseEntry>> = ExpenseDAO.getAllExpenses()//chat



}