package com.jacob.budgetbowl

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDAO {

    @Insert
    fun insertUser(userData: UserData): Long

    @Query("SELECT * FROM userDB")
    fun getAllUsers():List<UserData>

    @Query("SELECT * FROM userDB WHERE UserName = :aUserName ")
    fun getUsers(aUserName: String): UserData

    //maybe if we implement it it won't try and return cursor
    //ohhhhh wait a minute I'm being dumb
    //it's a query which gets something for us so of course we need a return type
    //no wait we're returning UserData
    //wait but the error is saying we're returning void the fuck?
    // and now it works but the parameter is being weird uggghh
    // now it works perfectly

}