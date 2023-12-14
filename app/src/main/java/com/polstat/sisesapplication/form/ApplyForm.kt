package com.polstat.sisesapplication.form

import kotlinx.serialization.Serializable

@Serializable
data class ApplyForm(
    val kelas: String,
    val divisi: String
)