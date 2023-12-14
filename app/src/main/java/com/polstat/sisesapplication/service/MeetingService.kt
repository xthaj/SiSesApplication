package com.polstat.sisesapplication.service

import com.polstat.sisesapplication.model.Meeting
import com.polstat.sisesapplication.model.User
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface MeetingService {
    @GET("meetings")
    suspend fun getAllMeetings(@Header("Authorization") token: String) : List<Meeting>
}