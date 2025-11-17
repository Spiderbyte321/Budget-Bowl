package com.jacob.budgetbowl

data class ExpenseEntry(
    val id: String = "",
    val expenseAmount: Int = 0,
    val expenseDate: String = "",
    val category: String = "",
    val expenseDescription: String = "",
    val imageUrl: String? = null
)
