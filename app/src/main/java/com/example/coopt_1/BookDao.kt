package com.example.coopt_1

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface BookDao {
    @Query("SELECT * FROM book")
    fun getBooks(): List<Book>

    @Upsert
    fun insertBook(vararg book: Book)
}