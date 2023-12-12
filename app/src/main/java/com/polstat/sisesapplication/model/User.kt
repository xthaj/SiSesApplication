package com.polstat.sisesapplication.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val username: String,
    val nama: String,
    val kelas: String?,
    val divisi: String?,
    val statusKeanggotaan: String?,
    val role: String?,
    val password: String? = null,

)