package com.example.retrofitapp.model.converters

import androidx.room.TypeConverter
import com.example.retrofitapp.model.Location
import com.google.gson.Gson

class LocationConverter {
    @TypeConverter
    fun fromLocation(location: Location): String {
        return Gson().toJson(location)
    }

    @TypeConverter
    fun toLocation(locationString: String): Location {
        return Gson().fromJson(locationString, Location::class.java)
    }
} 