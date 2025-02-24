package com.example.retrofitapp.model

data class Data(
    val characters: List<Character>,
    val currentPage: Int,
    val pageSize: Int,
    val total: Int
)