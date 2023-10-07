package com.example.coopt_1

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BookDao {
    @Query("SELECT * FROM book")
    fun getAll(): List<Book>

    @Insert(entity = Book::class)
    fun insertBook(book: Book)
}