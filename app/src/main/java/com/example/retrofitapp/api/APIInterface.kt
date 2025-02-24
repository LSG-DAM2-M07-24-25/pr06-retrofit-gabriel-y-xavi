package com.example.retrofitapp.api

import com.example.retrofitapp.model.Data
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.concurrent.TimeUnit

interface APIInterface {

    @GET("character")
    suspend fun getCharacters(): Response<Data>

    companion object {
        private const val BASE_URL = "https://rickandmortyapi.com/api/"
        
        fun create(): APIInterface {
            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(APIInterface::class.java)
        }
    }

}