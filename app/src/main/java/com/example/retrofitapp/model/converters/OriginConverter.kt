package com.example.retrofitapp.model.converters

import androidx.room.TypeConverter
import com.example.retrofitapp.model.Origin
import com.google.gson.Gson

class OriginConverter {
    @TypeConverter
    fun fromOrigin(origin: Origin): String {
        return Gson().toJson(origin)
    }

    @TypeConverter
    fun toOrigin(originString: String): Origin {
        return Gson().fromJson(originString, Origin::class.java)
    }
} 