package com.jacob.budgetbowl

import android.graphics.Bitmap
import android.graphics.Picture
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date


@Entity(tableName = "Expense")
data class ExpenseEntry(
  @PrimaryKey(autoGenerate = true) val id: Long,
    var ExpenseAmount : Int,
    var ExpenseDate : Date,
    var CatId : ECategory,
    var ExpenseImage: Bitmap
)
