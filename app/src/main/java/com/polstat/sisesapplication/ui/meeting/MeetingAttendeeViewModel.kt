package com.polstat.sisesapplication.ui.meeting

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.polstat.sisesapplication.SiSesApplication
import com.polstat.sisesapplication.repository.MeetingRepository
import com.polstat.sisesapplication.repository.UserPreferencesRepository
import com.polstat.sisesapplication.model.MeetingAttendee
import kotlinx.coroutines.launch

private const val TAG = "MeetingAttendeeViewModel"

class MeetingAttendeeViewModel (
    savedStateHandle: SavedStateHandle,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val meetingRepository: MeetingRepository
) : ViewModel() {

    private lateinit var token: String
    private val meetingId: Int = checkNotNull(savedStateHandle["meetingId"])
    private lateinit var attendees: List<MeetingAttendee>
    var selectedUsername: String by mutableStateOf("")
    var meetingAttendeeUiState: MeetingAttendeeUiState by mutableStateOf(MeetingAttendeeUiState.Loading)
        private set
    init {
        viewModelScope.launch {
            userPreferencesRepository.user.collect { user ->
                token = user.token
            }
        }
        getMeetingAttendees()
    }

    fun getMeetingAttendees() {
        viewModelScope.launch {
            meetingAttendeeUiState = MeetingAttendeeUiState.Loading
            try {
                attendees = meetingRepository.getAttendeesByMeeting(token, meetingId)
                meetingAttendeeUiState = MeetingAttendeeUiState.Success(attendees)
            } catch(e: Exception) {
                Log.e(TAG, "Exception: ${e.message}")
                meetingAttendeeUiState = MeetingAttendeeUiState.Error
            }
        }
    }

    suspend fun deleteMeetingAttendee(): DeleteMeetingAttendeeResult {
        try {
            meetingRepository.deleteMeetingAttendee(token, meetingId, selectedUsername)
        } catch (e: Exception) {
            Log.e(TAG, "exception: ${e.message}")
            return DeleteMeetingAttendeeResult.Error
        }
        return DeleteMeetingAttendeeResult.Success
    }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as SiSesApplication)
                MeetingAttendeeViewModel(
                    this.createSavedStateHandle(),
                    userPreferencesRepository = application.userPreferenceRepository,
                    meetingRepository = application.container.meetingRepository
                )
            }
        }
    }

}

sealed interface MeetingAttendeeUiState {
    data class Success(val attendees: List<MeetingAttendee>): MeetingAttendeeUiState
    object Error: MeetingAttendeeUiState
    object Loading: MeetingAttendeeUiState
}

enum class DeleteMeetingAttendeeResult {
    Success,
    Error
}