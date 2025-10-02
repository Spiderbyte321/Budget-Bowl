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
abstract class AppDatabase: RoomDatabase() {

    abstract fun userDAO(): UserDAO

    abstract fun expenseDAO(): ExpenseDAO


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
}