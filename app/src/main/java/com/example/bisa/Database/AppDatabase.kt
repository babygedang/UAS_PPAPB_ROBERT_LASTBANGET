package com.example.bisa.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/*
 * Database class using Room for local storage of FoodEntity objects.
 * The database has a single table for FoodEntity and can be accessed through FoodDao.
 */
@Database(entities = [FoodEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // Abstract method to obtain the FoodDao interface for database operations
    abstract fun foodDao(): FoodDao?

    companion object {
        // Volatile ensures that the value of INSTANCE is always up-to-date
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /*
         * Get an instance of the AppDatabase. If it doesn't exist, create a new one.
         * This method ensures that only one instance of the database is created.
         */
        fun getDatabase(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java, "makanan_database"
                    ).build()
                }
            }
            return INSTANCE
        }
    }
}
