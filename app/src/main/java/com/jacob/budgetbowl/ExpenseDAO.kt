package com.jacob.budgetbowl

import androidx.room.Insert

interface ExpenseDAO {

    @Insert
    fun insertExpense(expenseEntry: ExpenseEntry):Long




}