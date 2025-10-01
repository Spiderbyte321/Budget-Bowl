package com.jacob.budgetbowl

import android.graphics.Bitmap
import android.graphics.Picture
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "Expense")
data class ExpenseEntry(
  @PrimaryKey(autoGenerate = true) val id: Long=0,
    var ExpenseAmount : Int,
    var ExpenseDate : Date,
    var CatId : ECategory,
    var ExpesnseDescription : String
)
