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

    private val db = AppDatabase.getDatabase(application.applicationContext) as AppDatabase
     val ExpenseDAO = db.expenseDAO()

    val UserExpenses: LiveData<List<ExpenseEntry>> = ExpenseDAO.getAllExpenses()//chat

    var ListExpense: List<ExpenseEntry> = mutableListOf()



}