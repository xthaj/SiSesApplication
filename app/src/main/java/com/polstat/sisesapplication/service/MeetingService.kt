package com.polstat.sisesapplication.service

import com.polstat.sisesapplication.form.CreateMeetingForm
import com.polstat.sisesapplication.form.EditMeetingForm
import com.polstat.sisesapplication.model.Meeting
import com.polstat.sisesapplication.model.MeetingAttendee
import com.polstat.sisesapplication.response.ApiResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface MeetingService {
    @GET("meetings")
    suspend fun getAllMeetings(
        @Header("Authorization") token: String,
        @Query("sort") sortOrder: String? = null,
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null
    ) : List<Meeting>

    @GET("meetings/{id}")
    suspend fun getMeeting(@Header("Authorization") token: String, @Path("id") id: Int) : Meeting
    @PUT("meetings/{id}")
    suspend fun editMeeting(@Header("Authorization") token: String, @Path("id") id: Int, @Body form: EditMeetingForm) : Meeting
    @DELETE("meetings/{id}")
    suspend fun deleteMeeting(@Header("Authorization") token: String, @Path("id") id: Int) : ApiResponse
    @POST("meetings")
    suspend fun createMeeting(@Header("Authorization") token: String, @Body form: CreateMeetingForm) : Meeting
    @GET("meetings/{id}/members")
    suspend fun getAttendeesByMeeting(@Header("Authorization") token: String, @Path("id") id: Int) : List<MeetingAttendee>
    @POST("meetings/{id}/members")
    suspend fun attendMeeting(@Header("Authorization") token: String, @Path("id") id: Int) : ApiResponse
    @DELETE("meetings/{meeting_id}/members/{username}")
    suspend fun deleteMeetingAttendee(@Header("Authorization") token: String, @Path("meeting_id") meetingId: Int, @Path("username") username: String): ApiResponse
}