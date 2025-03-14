package com.example.retrofitapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.retrofitapp.model.converters.LocationConverter
import com.example.retrofitapp.model.converters.OriginConverter

@Entity(tableName = "characters")
data class CharacterEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    @TypeConverters(OriginConverter::class)
    val origin: Origin,
    @TypeConverters(LocationConverter::class)
    val location: Location,
    val image: String,
    val url: String,
    val created: String
)

fun Result.toEntity() = CharacterEntity(
    id = id,
    name = name,
    status = status,
    species = species,
    type = type,
    gender = gender,
    origin = origin,
    location = location,
    image = image,
    url = url,
    created = created
)

fun CharacterEntity.toResult() = Result(
    id = id,
    name = name,
    status = status,
    species = species,
    type = type,
    gender = gender,
    origin = origin,
    location = location,
    image = image,
    url = url,
    created = created
) 