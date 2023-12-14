package com.polstat.sisesapplication.ui.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.polstat.sisesapplication.SiSesApplication
import com.polstat.sisesapplication.data.UserRepository
import com.polstat.sisesapplication.form.RegisterForm

enum class RegisterResult {
    Success,
    EmptyField,
    PasswordMismatch,
    NetworkError
}

class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {

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
            userRepository.register(RegisterForm(usernameField, passwordField, nameField))
        } catch (e: Exception) {
            return RegisterResult.NetworkError
        }

        return RegisterResult.Success
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SiSesApplication)
                val userRepository = application.container.userRepository
                RegisterViewModel(userRepository = userRepository)
            }
        }
    }

}