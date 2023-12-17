package com.polstat.sisesapplication.form

import kotlinx.serialization.Serializable

@Serializable
data class CreateMeetingForm(
    val meetingName: String,
    val meetingSummary: String,
    val ruang: String
)