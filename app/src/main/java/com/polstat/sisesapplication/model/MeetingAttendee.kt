package com.polstat.sisesapplication.model

import kotlinx.serialization.Serializable

@Serializable
data class MeetingAttendee(
    val username: String,
    val name: String,
    val divisi: String,
    val time: String
)