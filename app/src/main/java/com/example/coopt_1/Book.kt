package com.example.coopt_1

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Book(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "isbn") val isbn: String?
)
