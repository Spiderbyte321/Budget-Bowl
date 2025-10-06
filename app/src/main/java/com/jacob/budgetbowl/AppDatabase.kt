package com.jacob.budgetbowl

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

//Reference Module manual
//Type converters
//https://developer.android.com/training/data-storage/room/referencing-data
@Database(entities = [UserData::class, ExpenseEntry::class],version=1)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {//(Google,S.A)

    abstract fun userDAO(): UserDAO//(Moodley,2025)

    abstract fun expenseDAO(): ExpenseDAO//(Moodley,2025)


    companion object{

        @Volatile
        private var INSTANCE: AppDatabase? =null

        fun getDatabase(context: Context): Any{
            return INSTANCE?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "user_database"
                ).build()
                INSTANCE = instance
                instance
            }

        }
    }

    ////References
    // Android Open Source Project. 10 February 2025.Referencing complex data using Room.[Online] Avaliable at:https://developer.android.com/training/data-storage/room/referencing-data [ Accessed on: 26 September 2025]
    //// The Independent Institute of Education. 2025. Open Source Coding Module Manuel  [OPSC 7311]. nt. [online via internal VLE] The Independent Institute of Education. Available at: https://advtechonline.sharepoint.com/:w:/r/sites/TertiaryStudents/_layouts/15/Doc.aspx?sourcedoc=%7BD5C243B5-895D-4B63-B083-140930EF9734%7D&file=OPSC7311MM.docx&action=default&mobileredirect=true [Accessed on: 30 September 2025].

}