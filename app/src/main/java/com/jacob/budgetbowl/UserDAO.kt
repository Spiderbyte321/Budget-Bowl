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

    //@Query("SELECT * FROM USERDB WHERE userName=:userName")
    //fun getUser(userName:String)

}