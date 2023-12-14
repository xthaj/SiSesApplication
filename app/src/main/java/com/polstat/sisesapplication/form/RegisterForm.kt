package com.polstat.sisesapplication.form

import kotlinx.serialization.Serializable

@Serializable
data class RegisterForm(
    val username: String,
    val password: String,
    val nama: String
)