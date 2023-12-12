package com.polstat.sisesapplication.form

import kotlinx.serialization.Serializable

@Serializable
data class LoginForm(
    val username: String,
    val password: String
)