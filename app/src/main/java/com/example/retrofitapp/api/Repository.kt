package com.example.retrofitapp.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository {

    private val apiInterface = APIInterface.create()

    suspend fun getAllCharacters() = withContext(Dispatchers.IO) {
        apiInterface.getCharacters()
    }
}