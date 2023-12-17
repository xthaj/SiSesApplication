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
import com.polstat.sisesapplication.form.EditMeetingForm
import com.polstat.sisesapplication.model.Meeting
import kotlinx.coroutines.launch

private const val TAG = "EditMeetingViewModel"

class EditMeetingViewModel(
    savedStateHandle: SavedStateHandle,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val meetingRepository: MeetingRepository
) : ViewModel() {
    var meetingUiState by mutableStateOf(Meeting(
        meetingId = 0,
        meetingName = "",
        meetingDate = "",
        ruang = "",
        meetingSummary = "",
        )
    )
        private set

    private lateinit var token: String
    private val meetingId: Int = checkNotNull(savedStateHandle["meetingId"])

    var meetingNameField by mutableStateOf("")
        private set

    var meetingSummaryField by mutableStateOf("")
        private set

    var meetingDateField by mutableStateOf("")
        private set

    var ruangField by mutableStateOf(0)
        private set

    fun updateMeetingNameField(meetingName: String) {
        meetingNameField = meetingName
    }

    fun updateMeetingSummaryField(summary: String) {
        meetingSummaryField = summary
    }

    fun updateMeetingDateField(date: String) {
        meetingDateField = date
    }

    fun updateRuangField(ruang: Int) {
        ruangField = ruang
    }

    init {
        viewModelScope.launch {
            userPreferencesRepository.user.collect { user ->
                token = user.token
            }
        }
        getMeetingData()
        //collect the stuff??
    }

    private fun getMeetingData() {
        viewModelScope.launch {
            val meeting = meetingRepository.getMeeting(token, meetingId)
            meetingUiState = meeting
            updateMeetingNameField(meeting.meetingName)
            updateMeetingSummaryField(meeting.meetingSummary)
            updateMeetingDateField(meeting.meetingDate)
            updateRuangField(meeting.ruang.toInt())
        }
    }

    suspend fun updateMeetingData(): UpdateMeetingDataResult{
        try {
            meetingRepository.editMeeting(
                token = token,
                id = meetingId,
                form = EditMeetingForm(
                    meetingName = meetingNameField,
                    meetingSummary = meetingSummaryField,
                    ruang = ruangField,
                    meetingDate = meetingDateField
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
            return UpdateMeetingDataResult.Error
        }
        return UpdateMeetingDataResult.Success
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as SiSesApplication)
                EditMeetingViewModel(
                    this.createSavedStateHandle(),
                    userPreferencesRepository = application.userPreferenceRepository,
                    meetingRepository = application.container.meetingRepository
                )
            }
        }
    }

}

enum class UpdateMeetingDataResult {
    Success,
    Error
}