package com.example.retrofitapp.model.dao

import androidx.room.*
import com.example.retrofitapp.model.CharacterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {
    @Query("SELECT * FROM characters")
    fun getAllCharacters(): Flow<List<CharacterEntity>>

    @Query("SELECT * FROM characters WHERE id = :id")
    suspend fun getCharacterById(id: Int): CharacterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacters(characters: List<CharacterEntity>)

    @Query("DELETE FROM characters")
    suspend fun deleteAllCharacters()

    @Query("SELECT * FROM characters WHERE name LIKE '%' || :query || '%'")
    fun searchCharacters(query: String): Flow<List<CharacterEntity>>
} 