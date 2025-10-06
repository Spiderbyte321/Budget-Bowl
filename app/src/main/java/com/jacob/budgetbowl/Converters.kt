package com.jacob.budgetbowl

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream


//https://developer.android.com/training/data-storage/room/referencing-data
//this whole class
//https://stackoverflow.com/questions/46666308/how-to-convert-imageview-to-bytearray-in-kotlin
//Bitmap to byte array
//https://stackoverflow.com/questions/11613594/android-how-to-convert-byte-array-to-bitmap
//Byte Array to BitMap
class Converters {//(Google,S.A)

    @TypeConverter
    fun FromBitmapToByte(bitmapInput: Bitmap?): ByteArray?{
        if(bitmapInput == null)
            return null
        val stream = ByteArrayOutputStream()
        bitmapInput.compress(Bitmap.CompressFormat.PNG,100,stream)
        return stream.toByteArray()
    }//(Best Best,2017)

    @TypeConverter
    fun toBitMapFromByte(bytesArray: ByteArray?): Bitmap?{
        if(bytesArray == null)
            return null
        return BitmapFactory.decodeByteArray(bytesArray,0,bytesArray.size)
    }//(AAnKit,2012)

}