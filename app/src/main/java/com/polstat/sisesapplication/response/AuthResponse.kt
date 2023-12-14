package com.polstat.sisesapplication.response

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String
)