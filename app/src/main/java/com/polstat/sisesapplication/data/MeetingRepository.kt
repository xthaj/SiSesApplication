package com.polstat.sisesapplication.data

import com.polstat.sisesapplication.form.ChangePasswordForm
import com.polstat.sisesapplication.form.LoginForm
import com.polstat.sisesapplication.form.RegisterForm
import com.polstat.sisesapplication.model.Meeting
import com.polstat.sisesapplication.model.User
import com.polstat.sisesapplication.response.ApiResponse
import com.polstat.sisesapplication.response.AuthResponse
import com.polstat.sisesapplication.service.MeetingService
import com.polstat.sisesapplication.service.UserService

interface MeetingRepository {
    suspend fun getAllMeetings(token: String): List<Meeting>
}

class NetworkMeetingRepository(private val meetingService: MeetingService) : MeetingRepository {
    override suspend fun getAllMeetings(token: String): List<Meeting> = meetingService.getAllMeetings("Bearer $token")
}
