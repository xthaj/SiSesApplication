package com.polstat.sisesapplication.form

import kotlinx.serialization.Serializable

@Serializable
data class UserForm(
    val username: String,
    val nama: String,
    val kelas: String?,
    val divisi: String?,
    val role: String?,
    val statusKeanggotaan: String?,

)