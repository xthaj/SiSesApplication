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
import com.polstat.sisesapplication.form.CreateMeetingForm
import kotlinx.coroutines.launch

private const val TAG = "CreateMeetingViewModel"

class CreateMeetingViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val meetingRepository: MeetingRepository
) : ViewModel() {
    private lateinit var token: String

    var meetingNameField by mutableStateOf("")
        private set
    var meetingSummaryField by mutableStateOf("")
        private set
    var ruangField by mutableStateOf("")
        private set

    fun updateMeetingNameField(meetingName: String) {
        meetingNameField = meetingName
    }

    fun updateMeetingSummaryField(summary: String) {
        meetingSummaryField = summary
    }
    fun updateRuangField(ruang: String) {
        ruangField = ruang
    }

    init {
        viewModelScope.launch {
            userPreferencesRepository.user.collect { user ->
                token = user.token
            }
        }
    }

    suspend fun createMeeting(): CreateMeetingDataResult{
        if (meetingNameField == "" || meetingSummaryField == "" || ruangField == "") {
            return CreateMeetingDataResult.EmptyField
        }
        if (!ruangField.matches(Regex("^[2][2-6][1-8]$|^[3][2-4][1-8]$"))) {
            return CreateMeetingDataResult.WrongRuangFormat
        }
        try {
            meetingRepository.createMeeting(
                token = token,
                form = CreateMeetingForm(
                    meetingName = meetingNameField,
                    meetingSummary = meetingSummaryField,
                    ruang = ruangField
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
            return CreateMeetingDataResult.Error
        }
        return CreateMeetingDataResult.Success
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as SiSesApplication)
                CreateMeetingViewModel(
                    userPreferencesRepository = application.userPreferenceRepository,
                    meetingRepository = application.container.meetingRepository
                )
            }
        }
    }

}

enum class CreateMeetingDataResult {
    Success,
    Error,
    EmptyField,
    WrongRuangFormat
}