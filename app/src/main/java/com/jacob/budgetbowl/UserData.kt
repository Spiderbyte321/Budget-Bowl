package com.jacob.budgetbowl


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "userDB")
data class UserData(
    @PrimaryKey(autoGenerate = true) val id:Long=0,
    var UserName: String,
    var NameSurname: String,
    var Password: String,
    var MinBudget: Int,
    var MaxBudget: Int,
    var TargetBudget: Int,
)
