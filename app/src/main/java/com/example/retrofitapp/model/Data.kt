package com.example.retrofitapp.model

data class Data(
    val currentPage: Int,
    val pageSize: Int,
    val total: Int,
    val characters: List<Result>
)