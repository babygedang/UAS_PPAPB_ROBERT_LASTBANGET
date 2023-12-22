package com.example.bisa.Database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

/*
 * Data Access Object (DAO) interface for database operations related to the FoodEntity.
 * It includes methods for inserting, updating, deleting, and querying FoodEntity objects.
 */
@Dao
interface FoodDao {

    // Insert a new FoodEntity into the database. OnConflictStrategy.IGNORE ignores duplicate entries.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(makanan: FoodEntity)

    // Update an existing FoodEntity in the database.
    @Update
    fun update(makanan: FoodEntity)

    // Delete a FoodEntity from the database.
    @Delete
    fun delete(makanan: FoodEntity)

    // Query to get all FoodEntity items from the database, ordered by their IDs in ascending order.
    @get:Query("SELECT * FROM makanan_table ORDER BY id ASC")
    val getAllMakanan: LiveData<List<FoodEntity>>
}

