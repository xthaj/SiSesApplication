package com.polstat.sisesapplication.ui.applicant

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.polstat.sisesapplication.SiSesApplication
import com.polstat.sisesapplication.data.UserPreferencesRepository
import com.polstat.sisesapplication.data.UserRepository
import com.polstat.sisesapplication.form.ApplyForm
import com.polstat.sisesapplication.form.ChangePasswordForm
import com.polstat.sisesapplication.model.User
import com.polstat.sisesapplication.ui.profile.UpdateProfileResult
import kotlinx.coroutines.launch
import retrofit2.HttpException

private const val TAG = "ApplyViewModel"

class ApplyViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val userRepository: UserRepository


) : ViewModel() {

    private lateinit var token: String
    private lateinit var username: String

    var showConfirmDialog by mutableStateOf(false)

    var kelasField by mutableStateOf("")
        private set

    var divisiField by mutableStateOf("")
        private set

    val divisiOptions = listOf("SPEECH", "STORY_TELLING", "DEBATE")

    var selectedDivisi = mutableStateOf("Select a Division")
    init {
        viewModelScope.launch {
            userPreferencesRepository.user.collect { user ->
                token = user.token
                username = user.username
                kelasField = user.kelas
                divisiField = user.divisi
            }
        }
    }

    fun updateKelasField(kelas: String) {
        kelasField = kelas
    }

    fun updateDivisiField(divisi: String) {
        divisiField = divisi
    }

    suspend fun apply(): ApplyResult {
        try {
            Log.e(TAG, kelasField)
            Log.e(TAG, divisiField)

            userRepository.apply(
                token = token,
                form = ApplyForm(
                    kelas = kelasField,
                    divisi = divisiField,
                )
            )

            userPreferencesRepository.saveDivisi(divisiField)
            userPreferencesRepository.saveStatusKeanggotaan("PENDAFTAR")
            userPreferencesRepository.saveKelas(kelasField)
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
            return ApplyResult.Error
        }
        return ApplyResult.Success
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SiSesApplication)
                ApplyViewModel(
                    userPreferencesRepository = application.userPreferenceRepository,
                    userRepository = application.container.userRepository
                )
            }
        }
    }
}

enum class ApplyResult {
    Success,
    Error
}