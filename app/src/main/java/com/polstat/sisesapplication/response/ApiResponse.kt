package com.polstat.sisesapplication.response

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse(
    val code: Int,
    val message: String
)