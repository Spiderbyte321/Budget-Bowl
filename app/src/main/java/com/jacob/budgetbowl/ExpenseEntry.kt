package com.jacob.budgetbowl


import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Expense")
data class ExpenseEntry(
  @PrimaryKey(autoGenerate = true) val id: Long=0,
    var ExpenseAmount : Int,
    var ExpenseDate : String,
    var CatId : ECategory,
    var ExpesnseDescription : String,
    var AddedImage : Bitmap? = null
)
