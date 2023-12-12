package com.polstat.sisesapplication.ui

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.polstat.sisesapplication.SiSesApplication
import com.polstat.sisesapplication.data.UserPreferencesRepository
import com.polstat.sisesapplication.data.UserState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update



class SiSesAppViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel(){

    private val _uiState = MutableStateFlow(SiSesAppUiState())
    val uiState: StateFlow<SiSesAppUiState> = _uiState.asStateFlow()

    val userState: StateFlow<UserState> = userPreferencesRepository.user.map { user -> user
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UserState(
            "",
            "",
            "",
            "",
            "",
            isAdmin = false,
            "",
        )
    )

    fun showSpinner() {
        _uiState.update { currentState ->
            currentState.copy(
                showProgressDialog = true
            )
        }
    }

    fun dismissSpinner() {
        _uiState.update { currentState ->
            currentState.copy(
                showProgressDialog = false
            )
        }
    }

    fun showMessageDialog(@StringRes title: Int, @StringRes body: Int) {
        _uiState.update {  currentState ->
            currentState.copy(
                showProgressDialog = false,
                showMessageDialog = true,
                messageTitle = title,
                messageBody = body
            )
        }
    }

    fun dismissMessageDialog() {
        _uiState.update {  currentState ->
            currentState.copy(
                showMessageDialog = false
            )
        }
    }

    suspend fun logout() {
        userPreferencesRepository.saveToken("")
        userPreferencesRepository.saveUsername("")
        userPreferencesRepository.saveName("")
        userPreferencesRepository.saveIsAdmin(false)
        userPreferencesRepository.saveDivisi("")
        userPreferencesRepository.saveStatusKeanggotaan("")
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as SiSesApplication)
                SiSesAppViewModel(
                    userPreferencesRepository = application.userPreferenceRepository
                )
            }
        }
    }
}

data class SiSesAppUiState(
    val showProgressDialog: Boolean = false,
    val showMessageDialog: Boolean = false,
    @StringRes val messageTitle: Int = 0,
    @StringRes val messageBody: Int = 0
)