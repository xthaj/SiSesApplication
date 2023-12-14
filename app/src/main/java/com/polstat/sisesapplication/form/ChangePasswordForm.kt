package com.polstat.sisesapplication.form

import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswordForm(
    val currentPassword: String,
    val newPassword: String,
    val confirmationPassword: String
)