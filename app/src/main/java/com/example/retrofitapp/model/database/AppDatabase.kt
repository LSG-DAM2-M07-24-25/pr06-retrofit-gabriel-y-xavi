package com.example.retrofitapp.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.retrofitapp.model.CharacterEntity
import com.example.retrofitapp.model.converters.LocationConverter
import com.example.retrofitapp.model.converters.OriginConverter
import com.example.retrofitapp.model.dao.CharacterDao

@Database(entities = [CharacterEntity::class], version = 1)
@TypeConverters(LocationConverter::class, OriginConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "rick_and_morty_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
} 