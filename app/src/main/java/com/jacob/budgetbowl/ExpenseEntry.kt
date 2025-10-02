package com.jacob.budgetbowl


import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "Expense")
data class ExpenseEntry(
  @PrimaryKey(autoGenerate = true) val id: Long=0,
    var ExpenseAmount : Int,
    var ExpenseDate : String,
    var CatId : ECategory,
    var ExpesnseDescription : String
)
