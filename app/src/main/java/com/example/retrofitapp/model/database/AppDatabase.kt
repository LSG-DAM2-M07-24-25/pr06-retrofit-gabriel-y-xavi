package com.example.retrofitapp.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.retrofitapp.model.CharacterEntity
import com.example.retrofitapp.model.converters.LocationConverter
import com.example.retrofitapp.model.converters.OriginConverter
import com.example.retrofitapp.model.dao.CharacterDao

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE characters ADD COLUMN isFavorite INTEGER NOT NULL DEFAULT 0")
    }
}

@Database(entities = [CharacterEntity::class], version = 2)
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
                )
                .addMigrations(MIGRATION_1_2)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 