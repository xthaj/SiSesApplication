package com.polstat.sisesapplication.ui.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.polstat.sisesapplication.SiSesApplication
import com.polstat.sisesapplication.repository.UserPreferencesRepository
import com.polstat.sisesapplication.repository.UserRepository
import com.polstat.sisesapplication.form.RegisterForm
import retrofit2.HttpException

private const val TAG = "RegisterViewModel"

class RegisterViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    var usernameField by mutableStateOf("")
        private set
    var nameField by mutableStateOf("")
        private set
    var passwordField by mutableStateOf("")
        private set
    var confirmPasswordField by mutableStateOf("")
        private set
    fun updateNameField(name: String) {
        nameField = name
    }
    fun updateUsernameField(email: String) {
        usernameField = email
    }
    fun updatePasswordField(password: String) {
        passwordField = password
    }
    fun updateConfirmPasswordField(password: String) {
        confirmPasswordField = password
    }

    suspend fun register(): RegisterResult {
        if (nameField == "" || usernameField == "" || passwordField == "") {
            return RegisterResult.EmptyField
        }
        if (passwordField != confirmPasswordField) {
            return RegisterResult.PasswordMismatch
        }
        try {
            val authResponse = userRepository.register(RegisterForm(usernameField, passwordField, nameField))
            userPreferencesRepository.saveToken(authResponse.token)
        } catch (e: HttpException) {
            when (e.code()) {
                409 -> {
                    return RegisterResult.UsernameNotUnique
                }
            }
        }
        catch (e: Exception) {
            return RegisterResult.NetworkError
        }

        userPreferencesRepository.saveUsername(usernameField)
        userPreferencesRepository.saveName(nameField)
        userPreferencesRepository.saveDivisi("")
        userPreferencesRepository.saveKelas("")
        userPreferencesRepository.saveStatusKeanggotaan("BUKAN_ANGGOTA")
        return RegisterResult.Success
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SiSesApplication)
                val userRepository = application.container.userRepository
                RegisterViewModel(
                    userPreferencesRepository = application.userPreferenceRepository,
                    userRepository = userRepository
                )
            }
        }
    }

}

enum class RegisterResult {
    Success,
    EmptyField,
    PasswordMismatch,
    UsernameNotUnique,
    NetworkError
}