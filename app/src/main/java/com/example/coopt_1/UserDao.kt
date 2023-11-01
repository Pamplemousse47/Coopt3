package com.example.coopt_1

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    //Retrieves all users
    @Query("SELECT * FROM user")
    fun getAll(): List<User>

    //Check if the user is already saved
    @Query("SELECT EXISTS(SELECT * FROM user WHERE user = :userrecieved)")
    fun doesUserExist(userrecieved: String) : Boolean

    //Deletes a single user
    @Delete
    fun delete(entry: User)

    //Inserts a new user
    @Insert(entity = User::class)
    fun insertUser(user: User)
}