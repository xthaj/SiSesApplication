package com.polstat.sisesapplication.ui.login

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.polstat.sisesapplication.SiSesApplication
import com.polstat.sisesapplication.data.UserPreferencesRepository
import com.polstat.sisesapplication.data.UserRepository
import com.polstat.sisesapplication.form.LoginForm
import retrofit2.HttpException

private const val TAG = "LoginViewModel"

class LoginViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    var usernameField by mutableStateOf("")
        private set

    var passwordField by mutableStateOf("")
        private set

    fun updateUsername(username: String) {
        usernameField = username
    }

    fun updatePassword(password: String) {
        passwordField = password
    }

    suspend fun attemptLogin(): LoginResult {
        try {
            Log.d(TAG, "uname: $usernameField")
            Log.d(TAG, "password: $passwordField")
            val loginResponse = userRepository.login(LoginForm(usernameField, passwordField))
            Log.d(TAG, "accessToken: ${loginResponse.token}")

            userPreferencesRepository.saveToken(loginResponse.token)

//            Log.d(TAG, "hello1")
            val user = userRepository.getProfile(loginResponse.token, usernameField)
//            Log.d(TAG, "hello2")
            val isAdmin = user.role == "ADMIN"
//            Log.d(TAG, "username: ${user.username}")
//            Log.d(TAG, "name: ${user.nama}")
//            Log.d(TAG, "isAdmin: $isAdmin")
//            Log.d(TAG, "divisi: ${user.divisi}")

            userPreferencesRepository.saveUsername(user.username)
            userPreferencesRepository.saveName(user.nama)
            userPreferencesRepository.saveKelas(user.kelas ?: "")
            userPreferencesRepository.saveStatusKeanggotaan(user.statusKeanggotaan ?: "")
            userPreferencesRepository.saveIsAdmin(isAdmin)
            userPreferencesRepository.saveDivisi(user.divisi ?: "")
        } catch(e: HttpException) {
            when (e.code()) {
                400 -> {
                    Log.d(TAG, "bad input")
                    return LoginResult.BadInput
                }
                401 -> {
                    Log.d(TAG, "Wrong email or password")
                    return LoginResult.WrongEmailOrPassword
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Can't login: (${e.javaClass}) ${e.message}")
            Log.e(TAG, e.stackTraceToString())
            return LoginResult.NetworkError
        }

        return LoginResult.Success
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as SiSesApplication)
                val userRepository = application.container.userRepository
                LoginViewModel(
                    userPreferencesRepository = application.userPreferenceRepository,
                    userRepository = userRepository
                )
            }
        }
    }
}

enum class LoginResult {
    Success,
    BadInput,
    WrongEmailOrPassword,
    NetworkError
}