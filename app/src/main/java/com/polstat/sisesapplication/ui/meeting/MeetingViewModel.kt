package com.polstat.sisesapplication.ui.meeting

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
import com.polstat.sisesapplication.data.MeetingRepository
import com.polstat.sisesapplication.data.UserPreferencesRepository
import com.polstat.sisesapplication.data.UserRepository
import com.polstat.sisesapplication.form.ChangePasswordForm
import com.polstat.sisesapplication.model.Meeting
import com.polstat.sisesapplication.model.User
import com.polstat.sisesapplication.ui.profile.ProfileViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException

private const val TAG = "MeetingViewModel"

class MeetingViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val meetingRepository: MeetingRepository

) : ViewModel() {

    private lateinit var token: String
    private lateinit var meetings: List<Meeting>

    var meetingManagementUiState: MeetingManagementUiState by mutableStateOf(MeetingManagementUiState.Loading)
        private set

    init {
        viewModelScope.launch {
            userPreferencesRepository.user.collect { user ->
                token = user.token
            }
        }
        getAllMeetings()
    }

    fun getAllMeetings() {
        viewModelScope.launch {
            meetingManagementUiState = MeetingManagementUiState.Loading
            try {
                meetings = meetingRepository.getAllMeetings(token)
                meetingManagementUiState = MeetingManagementUiState.Success(meetings)
            } catch(e: Exception) {
                Log.e(TAG, "Exception: ${e.message}")
                meetingManagementUiState = MeetingManagementUiState.Error
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as SiSesApplication)
                MeetingViewModel(
                    userPreferencesRepository = application.userPreferenceRepository,
                    meetingRepository = application.container.meetingRepository
                )
            }
        }
    }
}

sealed interface MeetingManagementUiState {
    data class Success(val meetings: List<Meeting>): MeetingManagementUiState
    object Error: MeetingManagementUiState
    object Loading: MeetingManagementUiState
}
