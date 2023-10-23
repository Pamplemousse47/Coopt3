package com.example.coopt_1

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BookDao {
    //Retrieves all books
    @Query("SELECT * FROM book")
    fun getAll(): List<Book>

    //Check if the book is already saved
    @Query("SELECT EXISTS(SELECT * FROM book WHERE isbn = :isbn_recieved)")
    fun doesBookExist(isbn_recieved: String) : Boolean

    //Deletes a single book
    @Delete
    fun delete(entry: Book)

    //Inserts a new book
    @Insert(entity = Book::class)
    fun insertBook(book: Book)
}