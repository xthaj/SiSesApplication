package com.polstat.sisesapplication.data

import com.polstat.sisesapplication.form.ChangePasswordForm
import com.polstat.sisesapplication.form.LoginForm
import com.polstat.sisesapplication.form.RegisterForm
import com.polstat.sisesapplication.response.AuthResponse
import com.polstat.sisesapplication.model.User
import com.polstat.sisesapplication.response.ApiResponse
import com.polstat.sisesapplication.service.UserService

interface UserRepository {
    suspend fun register(user: RegisterForm)
    suspend fun login(form: LoginForm): AuthResponse
    suspend fun getProfile(token: String, username: String): User
    suspend fun updateUser(token: String, username: String, user: User): User
    suspend fun updatePassword(token: String, form: ChangePasswordForm): ApiResponse


}

class NetworkUserRepository(private val userService: UserService) : UserRepository {
    override suspend fun register(user: RegisterForm) = userService.register(user)
    override suspend fun login(form: LoginForm): AuthResponse = userService.login(form)
    override suspend fun getProfile(token: String, username: String): User = userService.getProfile("Bearer $token", username)
    override suspend fun updateUser(token: String, username: String, user: User): User {
        return userService.updateUser("Bearer $token", username, user)
    }
    override suspend fun updatePassword(token: String, form: ChangePasswordForm): ApiResponse = userService.updatePassword("Bearer $token", form)


}
