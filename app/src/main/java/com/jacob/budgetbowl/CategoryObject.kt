package com.jacob.budgetbowl

data class CategoryObject(
    val targetBudget: Int = 1,
    var categoryTotalExpenditure: Int = 0,
    var icon: Int? = null
)
