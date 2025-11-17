package com.jacob.budgetbowl

data class UserData(
    val id: String = "",
    var userName: String = "",
    var nameSurname: String = "",
    var minBudget: Int = 0,
    var maxBudget: Int = 0,
    var targetBudget: Int = 0,
    var points: Int = 0
)
