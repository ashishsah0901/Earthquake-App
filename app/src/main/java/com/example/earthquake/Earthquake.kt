package com.example.earthquake

data class Earthquake(
    val magnitude: Double,
    val place: String,
    val date: Long,
    val url: String
)