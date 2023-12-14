package com.polstat.sisesapplication.service

import com.polstat.sisesapplication.form.ApplyForm
import com.polstat.sisesapplication.form.ChangePasswordForm
import com.polstat.sisesapplication.form.LoginForm
import com.polstat.sisesapplication.form.RegisterForm
import com.polstat.sisesapplication.model.Meeting
import com.polstat.sisesapplication.response.AuthResponse
import com.polstat.sisesapplication.model.User
import com.polstat.sisesapplication.response.ApiResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserService {
    @POST("auth/register")
    suspend fun register(@Body user: RegisterForm)
    @POST("auth/authenticate")
    suspend fun login(@Body form: LoginForm): AuthResponse
    @GET("users/{username}")
    suspend fun getProfile(@Header("Authorization") token: String, @Path("username") username: String): User
    @PATCH("users")
    suspend fun updatePassword(@Header("Authorization") token: String, @Body form: ChangePasswordForm): ApiResponse
    @PUT("users/{username}")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Path("username") username: String,
        @Body user: User
    ): User

    // applications, daftar SES
    @GET("applicants")
    suspend fun getApplicants(@Header("Authorization") token: String) : List<User>
    @POST("applicants")
    suspend fun apply(@Header("Authorization") token: String, @Body form: ApplyForm) : ApiResponse
    @PATCH("applicants/{username}")
    suspend fun acceptApplicant(@Header("Authorization") token: String, @Path("username") username: String): User
    @DELETE("applicants/{username}")
    suspend fun declineApplicant(@Header("Authorization") token: String, @Path("username") username: String): User
}