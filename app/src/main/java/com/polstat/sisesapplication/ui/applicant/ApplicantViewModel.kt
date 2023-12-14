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
import com.polstat.sisesapplication.data.MeetingRepository
import com.polstat.sisesapplication.data.UserPreferencesRepository
import com.polstat.sisesapplication.data.UserRepository
import com.polstat.sisesapplication.model.Meeting
import com.polstat.sisesapplication.model.User
import com.polstat.sisesapplication.ui.meeting.MeetingViewModel
import kotlinx.coroutines.launch


private const val TAG = "ApplicantViewModel"

class ApplicantViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private lateinit var token: String
    private lateinit var applicants: List<User>
    var selectedUsername: String by mutableStateOf("")

    var applicantsManagementUiState: ApplicantsManagementUiState by mutableStateOf(ApplicantsManagementUiState.Loading)
        private set

    init {
        viewModelScope.launch {
            userPreferencesRepository.user.collect { user ->
                token = user.token
            }
        }
        getAllApplicants()
    }

    fun getAllApplicants() {
        viewModelScope.launch {
            applicantsManagementUiState = ApplicantsManagementUiState.Loading
            try {
                applicants = userRepository.getApplicants(token)
                applicantsManagementUiState = ApplicantsManagementUiState.Success(applicants)
            } catch(e: Exception) {
                Log.e(TAG, "Exception: ${e.message}")
                applicantsManagementUiState = ApplicantsManagementUiState.Error
            }
        }
    }

    suspend fun declineApplicant(): DeclineApplicantResult {
        try {
            userRepository.declineApplicant(token, selectedUsername)
        } catch (e: Exception) {
            Log.e(TAG, "exception: ${e.message}")
            return DeclineApplicantResult.Error
        }
        userPreferencesRepository.saveStatusKeanggotaan("BUKAN_ANGGOTA")
        userPreferencesRepository.saveDivisi("")
        return DeclineApplicantResult.Success
    }

    suspend fun acceptApplicant(): AcceptApplicantResult {
        try {
            userRepository.acceptApplicant(token, selectedUsername)
        } catch (e: Exception) {
            Log.e(TAG, "exception: ${e.message}")
            return AcceptApplicantResult.Error
        }
        userPreferencesRepository.saveStatusKeanggotaan("ANGGOTA")
        return AcceptApplicantResult.Success
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SiSesApplication)
                ApplicantViewModel(
                    userPreferencesRepository = application.userPreferenceRepository,
                    userRepository = application.container.userRepository
                )
            }
        }
    }
}

sealed interface ApplicantsManagementUiState {
    data class Success(val applicants: List<User>): ApplicantsManagementUiState
    object Error: ApplicantsManagementUiState
    object Loading: ApplicantsManagementUiState
}

enum class DeclineApplicantResult {
    Success,
    Error
}

enum class AcceptApplicantResult {
    Success,
    Error
}