package com.polstat.sisesapplication.data

import com.polstat.sisesapplication.form.LoginForm
import com.polstat.sisesapplication.model.LoginResponse
import com.polstat.sisesapplication.model.User
import com.polstat.sisesapplication.service.UserService

interface UserRepository {
    suspend fun login(form: LoginForm): LoginResponse

    suspend fun getProfile(token: String, username: String): User

    suspend fun updateUser(token: String, username: String, user: User): User

}

class NetworkUserRepository(private val userService: UserService) : UserRepository {
    override suspend fun login(form: LoginForm): LoginResponse = userService.login(form)

    override suspend fun getProfile(token: String, username: String): User = userService.getProfile("Bearer $token", username)

    override suspend fun updateUser(token: String, username: String, user: User): User {
        return userService.updateUser("Bearer $token", username, user)
    }

}
