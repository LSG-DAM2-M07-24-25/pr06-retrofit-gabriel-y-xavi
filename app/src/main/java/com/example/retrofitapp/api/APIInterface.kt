package com.example.retrofitapp.api

import com.example.retrofitapp.model.Data
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url

interface APIInterface {

    @GET("characters")
    suspend fun getCharacters(): Response<Data>

    companion object {
        val BASE_URL = "https://dattebayo-api.onrender.com/"
        fun create(): APIInterface {
            val client = OkHttpClient.Builder().build()
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(APIInterface::class.java)
        }
    }

}