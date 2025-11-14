package com.jacob.budgetbowl

import android.graphics.Bitmap

data class ExpenseObject(
    var ExpenseAmount : Int,
    var ExpenseDate : String,
    var CatId : String,
    var ExpesnseDescription : String,
    var AddedImageURL : String? = null
)
