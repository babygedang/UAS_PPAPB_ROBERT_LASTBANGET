package com.example.bisa.Database

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/*
 * Entity class representing the structure of the "makanan_table" in the database.
 * It is used by Room to create a SQLite table for storing FoodEntity objects.
 */
@Entity(tableName = "makanan_table")
data class FoodEntity (
    // Primary key with autoGenerate set to true for automatically generating unique IDs.
    @PrimaryKey(autoGenerate = true)
    @NonNull
    val id:Int = 0,

    // Column storing the name of the food.
    @ColumnInfo(name = "makanan")
    val makanan: String,

    // Column storing the calorie content of the food.
    @ColumnInfo(name = "kalori")
    val kalori: String
)
