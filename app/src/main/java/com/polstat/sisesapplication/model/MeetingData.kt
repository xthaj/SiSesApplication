package com.polstat.sisesapplication.model

import kotlinx.serialization.Serializable

@Serializable
data class MeetingData(
    val meetingName: String,
    val meetingDate: String,
    val ruang: String,
    val meetingSummary: String
)