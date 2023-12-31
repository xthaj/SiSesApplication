package com.polstat.sisesapplication.repository

import com.polstat.sisesapplication.form.ApplyForm
import com.polstat.sisesapplication.form.ChangePasswordForm
import com.polstat.sisesapplication.form.LoginForm
import com.polstat.sisesapplication.form.RegisterForm
import com.polstat.sisesapplication.response.AuthResponse
import com.polstat.sisesapplication.model.User
import com.polstat.sisesapplication.response.ApiResponse
import com.polstat.sisesapplication.service.UserService

interface UserRepository {
    suspend fun register(user: RegisterForm): AuthResponse
    suspend fun login(form: LoginForm): AuthResponse
    suspend fun getProfile(token: String, username: String): User
    suspend fun updateUser(token: String, username: String, user: User): User
    suspend fun updatePassword(token: String, form: ChangePasswordForm): ApiResponse
    suspend fun deleteUser(token: String, username: String)

    // applicants

    suspend fun getApplicants(token: String): List<User>
    suspend fun apply(token: String, form: ApplyForm): ApiResponse
    suspend fun declineApplicant(token: String, username: String): User
    suspend fun acceptApplicant(token: String, username: String): User


}

class NetworkUserRepository(private val userService: UserService) : UserRepository {
    override suspend fun register(user: RegisterForm): AuthResponse = userService.register(user)
    override suspend fun login(form: LoginForm): AuthResponse = userService.login(form)
    override suspend fun getProfile(token: String, username: String): User = userService.getProfile("Bearer $token", username)
    override suspend fun updateUser(token: String, username: String, user: User): User {
        return userService.updateUser("Bearer $token", username, user)
    }
    override suspend fun updatePassword(token: String, form: ChangePasswordForm): ApiResponse = userService.updatePassword("Bearer $token", form)
    override suspend fun deleteUser(token: String, username: String) = userService.deleteUser("Bearer $token", username)

    // applicants

    override suspend fun getApplicants(token: String): List<User> = userService.getApplicants("Bearer $token")
    override suspend fun apply(token: String, form: ApplyForm): ApiResponse = userService.apply("Bearer $token", form)
    override suspend fun declineApplicant(token: String, username: String): User = userService.declineApplicant("Bearer $token", username)
    override suspend fun acceptApplicant(token: String, username: String): User = userService.acceptApplicant("Bearer $token", username)


}
