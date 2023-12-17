package com.polstat.sisesapplication.form

import kotlinx.serialization.Serializable

@Serializable
data class EditMeetingForm(
    val meetingName: String,
    val meetingSummary: String,
    val ruang: Int,
    val meetingDate: String
)