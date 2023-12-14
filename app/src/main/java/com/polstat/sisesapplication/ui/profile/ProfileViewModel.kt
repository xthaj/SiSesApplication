package com.polstat.sisesapplication.ui.profile

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.polstat.sisesapplication.SiSesApplication
import com.polstat.sisesapplication.data.UserPreferencesRepository
import com.polstat.sisesapplication.data.UserRepository
import com.polstat.sisesapplication.form.ChangePasswordForm
import com.polstat.sisesapplication.model.User
import kotlinx.coroutines.launch
import retrofit2.HttpException

private const val TAG = "ProfileViewModel"

class ProfileViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val userRepository: UserRepository


) : ViewModel() {

    private lateinit var token: String
    private lateinit var username: String

    var showConfirmDialog by mutableStateOf(false)

    var nameField by mutableStateOf("")
        private set

    var kelasField by mutableStateOf("")
        private set

    var divisi by mutableStateOf("")
        private set

    var role by mutableStateOf("")
        private set

    var statusKeanggotaan by mutableStateOf("")
        private set

    var oldPasswordField by mutableStateOf("")
        private set

    var newPasswordField by mutableStateOf("")
        private set

    var confirmPasswordField by mutableStateOf("")
        private set

    init {
        viewModelScope.launch {
            userPreferencesRepository.user.collect { user ->
                token = user.token
                nameField = user.name
                username = user.username
                kelasField = user.kelas
                divisi = user.divisi
                statusKeanggotaan = user.statusKeanggotaan
                role = if (user.isAdmin) "ADMIN" else "USER"
            }
        }
    }

    fun updateNameField(name: String) {
        nameField = name
    }

    fun updateKelasField(kelas: String) {
        kelasField = kelas
    }

    fun updateOldPasswordField(password: String) {
        oldPasswordField = password
    }

    fun updateNewPasswordField(password: String) {
        newPasswordField = password
    }

    fun updateConfirmPasswordField(password: String) {
        confirmPasswordField = password
    }

    suspend fun updateProfile(): UpdateProfileResult {
        try {
            Log.e(TAG, "Before Profile - " +
                    "Username: $username, " +
                    "Name: $nameField, " +
                    "Kelas: $kelasField, " +
                    "Divisi: ${divisi ?: "null"}, " +
                    "Role: $role, " +
                    "Status Keanggotaan: $statusKeanggotaan")

            Log.e(TAG, "Token: $token")

            userRepository.updateUser(
                token = token,
                username = username,

                user = User(
                    username = username,
                    nama = nameField,
                    kelas = kelasField,
                    divisi = divisi.takeIf { it.isNotBlank() },
                    role = role,
                    statusKeanggotaan = statusKeanggotaan
                )
            )

            Log.e(TAG, "Updated Profile - " +
                    "Username: $username, " +
                    "Name: $nameField, " +
                    "Kelas: $kelasField, " +
                    "Divisi: $divisi, " +
                    "Role: $role, " +
                    "Status Keanggotaan: $statusKeanggotaan")


            userPreferencesRepository.saveName(nameField)
            userPreferencesRepository.saveKelas(kelasField)

        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
            return UpdateProfileResult.Error
        }

        return UpdateProfileResult.Success
    }

    suspend fun updatePassword(): UpdatePasswordResult {
        if (newPasswordField != confirmPasswordField) {
            return UpdatePasswordResult.Mismatch
        }

        try {
            userRepository.updatePassword(
                token,
                ChangePasswordForm(
                    currentPassword = oldPasswordField,
                    newPassword = newPasswordField,
                    confirmationPassword = newPasswordField
                )
            )
        } catch (e: HttpException) {
            return when (e.code()) {
                401 -> UpdatePasswordResult.WrongPassword
                else -> UpdatePasswordResult.Error
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
            return UpdatePasswordResult.Error
        }

        return UpdatePasswordResult.Success
    }

    suspend fun deleteAccount(): DeleteAccountResult {
        try {
//            userRepository.deleteProfile(token)
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
            return DeleteAccountResult.Error
        }

//        userPreferencesRepository.saveToken("")
//        userPreferencesRepository.saveName("")
//        userPreferencesRepository.saveEmail("")
//        userPreferencesRepository.saveIsAdmin(false)
//        userPreferencesRepository.saveIsSupervisor(false)
//        userPreferencesRepository.saveIsEnumerator(false)

        return DeleteAccountResult.Success
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as SiSesApplication)
                ProfileViewModel(
                    userPreferencesRepository = application.userPreferenceRepository,
                    userRepository = application.container.userRepository
                )
            }
        }
    }
}

enum class UpdateProfileResult {
    Success,
    Error
}

enum class UpdatePasswordResult {
    Success,
    WrongPassword,
    Mismatch,
    Error
}

enum class DeleteAccountResult {
    Success,
    Error
}