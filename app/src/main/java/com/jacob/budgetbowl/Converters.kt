package com.jacob.budgetbowl

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream


//https://developer.android.com/training/data-storage/room/referencing-data
//this whole class
//Safety's sake reference chat for the methods
class Converters {

    @TypeConverter
    fun FromBitmapToByte(bitmapInput: Bitmap?): ByteArray?{
        if(bitmapInput == null)
            return null
        val stream = ByteArrayOutputStream()
        bitmapInput.compress(Bitmap.CompressFormat.PNG,100,stream)
        return stream.toByteArray()
    }

    @TypeConverter
    fun toBitMapFromByte(bytesArray: ByteArray?): Bitmap?{
        if(bytesArray == null)
            return null
        return BitmapFactory.decodeByteArray(bytesArray,0,bytesArray.size)
    }

}