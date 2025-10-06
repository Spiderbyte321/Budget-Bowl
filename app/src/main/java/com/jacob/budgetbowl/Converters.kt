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


    ////References
    // AggelosK. 23 July 2012. Android: how to convert byte array to Bitmap.[Online] Avaliable at: //https://stackoverflow.com/questions/11613594/android-how-to-convert-byte-array-to-bitmap.[ Accessed on: 27 Spetember 2025]
    //// Android Open Source Project. 10 February 2025.Referencing complex data using Room.[Online] Avaliable at:https://developer.android.com/training/data-storage/room/referencing-data [ Accessed on: 26 September 2025]
    //// Xalara. 13 April 2025. How to convert imageview to byte array in kotlin. [Online] Avaliable at: https://stackoverflow.com/questions/46666308/how-to-convert-imageview-to-bytearray-in-kotlin. [Accessed on: 27 September 2025]


}