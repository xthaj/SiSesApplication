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
import com.polstat.sisesapplication.repository.MeetingRepository
import com.polstat.sisesapplication.repository.UserPreferencesRepository
import com.polstat.sisesapplication.model.Meeting
import kotlinx.coroutines.launch

private const val TAG = "MeetingViewModel"

class MeetingViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val meetingRepository: MeetingRepository

) : ViewModel() {

    private lateinit var token: String
    private lateinit var meetings: List<Meeting>
    var selectedMeetingId: Int by mutableStateOf(0)
    var sort: String by mutableStateOf("desc")
    var startDate: String by mutableStateOf("")
    var endDate: String by mutableStateOf("")


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

    fun getAllMeetings(sortOrder: String = sort, startDate: String = this.startDate, endDate: String = this.endDate) {        viewModelScope.launch {
            meetingManagementUiState = MeetingManagementUiState.Loading
            try {
                meetings = meetingRepository.getAllMeetings(token, sortOrder, startDate, endDate)
                meetingManagementUiState = MeetingManagementUiState.Success(meetings)
            } catch(e: Exception) {
                Log.e(TAG, "Exception: ${e.message}")
                meetingManagementUiState = MeetingManagementUiState.Error
            }
        }
    }

    suspend fun deleteMeeting(): DeleteMeetingResult {
        try {
            meetingRepository.deleteMeeting(token, selectedMeetingId)
        } catch (e: Exception) {
            Log.e(TAG, "exception: ${e.message}")
            return DeleteMeetingResult.Error
        }
        return DeleteMeetingResult.Success
    }

    suspend fun attendMeeting(): AttendMeetingResult {
//        Log.e(TAG, "Token: $token")
        Log.e(TAG, "Meeting id: $selectedMeetingId")
        Log.e(TAG, "Username: $selectedMeetingId")
        try {
            meetingRepository.attendMeeting(token, selectedMeetingId)
        } catch (e: Exception) {
            Log.e(TAG, "exception: ${e.message}")
            return AttendMeetingResult.Error
        }
        return AttendMeetingResult.Success
    }

    fun applyFilter(sortOrder: String, startDate: String, endDate: String) {
        Log.e(TAG, "sort order: $sortOrder")
        this.sort = sortOrder
        this.startDate = startDate
        this.endDate = endDate
        getAllMeetings()
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

enum class DeleteMeetingResult {
    Success,
    Error
}

enum class AttendMeetingResult {
    Success,
    Error
}