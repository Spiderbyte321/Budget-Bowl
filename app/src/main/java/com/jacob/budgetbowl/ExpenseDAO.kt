package com.jacob.budgetbowl

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface ExpenseDAO {

    @Insert
    fun insertExpense(expenseEntry: ExpenseEntry):Long




}