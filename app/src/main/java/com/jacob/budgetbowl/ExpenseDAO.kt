package com.jacob.budgetbowl

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ExpenseDAO {

    @Insert
    fun insertExpense(expenseEntry: ExpenseEntry):Long

    @Query("SELECT * FROM EXPENSE")
    fun getAllExpenses(): LiveData<List<ExpenseEntry>>

    @Query("SELECT * FROM EXPENSE")
    fun getAllExpensesASList(): List<ExpenseEntry>




}