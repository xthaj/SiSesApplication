package com.polstat.sisesapplication.data

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.polstat.sisesapplication.service.MeetingService
import com.polstat.sisesapplication.service.UserService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

interface AppContainer {
    val userRepository: UserRepository
    val meetingRepository: MeetingRepository
}
class DefaultAppContainer() : AppContainer {
    private val baseUrl = "http://10.0.2.2:8080/api/v1/"
    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl)
        .build()

    //user + applicants
    private val userService: UserService by lazy {
        retrofit.create(UserService::class.java)
    }
    override val userRepository: UserRepository by lazy {
        NetworkUserRepository(userService)
    }

    //meeting
    private val meetingService: MeetingService by lazy {
        retrofit.create(MeetingService::class.java)
    }
    override val meetingRepository: MeetingRepository by lazy {
        NetworkMeetingRepository(meetingService)
    }
}