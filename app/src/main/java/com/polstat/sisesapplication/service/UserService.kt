package com.polstat.sisesapplication.service

import com.polstat.sisesapplication.form.LoginForm
import com.polstat.sisesapplication.model.LoginResponse
import com.polstat.sisesapplication.model.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface UserService {
    @POST("auth/authenticate")
    suspend fun login(@Body form: LoginForm): LoginResponse
    @GET("users/{username}")
    suspend fun getProfile(@Header("Authorization") token: String, @Path("username") username: String): User

    @POST("users/{username}")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Path("username") username: String,
        @Body user: User
    ): User

//    (@Header("Authorization") token: String, @Body user: User): User
}