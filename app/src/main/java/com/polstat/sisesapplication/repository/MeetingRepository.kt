package com.polstat.sisesapplication.repository

import com.polstat.sisesapplication.form.CreateMeetingForm
import com.polstat.sisesapplication.form.EditMeetingForm
import com.polstat.sisesapplication.model.Meeting
import com.polstat.sisesapplication.model.MeetingAttendee
import com.polstat.sisesapplication.response.ApiResponse
import com.polstat.sisesapplication.service.MeetingService

interface MeetingRepository {
    suspend fun getAllMeetings(token: String, sortOrder: String?, startDate: String?, endDate: String?): List<Meeting>
    suspend fun getMeeting(token: String, id: Int): Meeting
    suspend fun editMeeting(token: String, id: Int, form: EditMeetingForm): Meeting
    suspend fun deleteMeeting(token: String, id: Int): ApiResponse
    suspend fun createMeeting(token: String, form: CreateMeetingForm): Meeting
    suspend fun getAttendeesByMeeting(token: String, id: Int): List<MeetingAttendee>
    suspend fun attendMeeting(token: String, id: Int): ApiResponse
    suspend fun deleteMeetingAttendee(token: String, meetingId: Int, username: String): ApiResponse
}

class NetworkMeetingRepository(private val meetingService: MeetingService) : MeetingRepository {
    override suspend fun getAllMeetings(token: String, sortOrder: String?, startDate: String?, endDate: String?): List<Meeting> =
        meetingService.getAllMeetings("Bearer $token", sortOrder, startDate, endDate)
    override suspend fun getMeeting(token: String, id: Int): Meeting =
        meetingService.getMeeting("Bearer $token", id)

    override suspend fun editMeeting(token: String, id: Int, form: EditMeetingForm): Meeting =
        meetingService.editMeeting("Bearer $token", id, form)

    override suspend fun deleteMeeting(token: String, id: Int): ApiResponse =
        meetingService.deleteMeeting("Bearer $token", id)

    override suspend fun createMeeting(token: String, form: CreateMeetingForm): Meeting =
        meetingService.createMeeting("Bearer $token", form)

    override suspend fun getAttendeesByMeeting(token: String, id: Int): List<MeetingAttendee> =
        meetingService.getAttendeesByMeeting("Bearer $token", id)

    override suspend fun attendMeeting(token: String, id: Int): ApiResponse =
        meetingService.attendMeeting("Bearer $token", id)

    override suspend fun deleteMeetingAttendee(token: String, meetingId: Int, username: String): ApiResponse =
        meetingService.deleteMeetingAttendee("Bearer $token", meetingId, username)
}


