package com.polstat.sisesapplication.ui.meeting

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.polstat.sisesapplication.R
import com.polstat.sisesapplication.ui.AttendeeCard
import com.polstat.sisesapplication.ui.ConfirmDialog
import com.polstat.sisesapplication.ui.DrawerNavigationItem
import com.polstat.sisesapplication.ui.MeetingCard
import com.polstat.sisesapplication.ui.SiSesScreen
import com.polstat.sisesapplication.ui.applicant.ApplicantList
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MeetingAttendeeScreen(
    meetingAttendeeViewModel: MeetingAttendeeViewModel = viewModel(factory = MeetingViewModel.Factory),
    modifier: Modifier = Modifier,
    showSpinner: () -> Unit = {},
    showMessage: (Int, Int) -> Unit = { _, _ -> },
    navController: NavHostController = rememberNavController()
) {
    val scope = rememberCoroutineScope()

    var showConfirmDialog by rememberSaveable { mutableStateOf(false) }

    if (showConfirmDialog) {
        ConfirmDialog(
            onConfirmRequest = {
                showConfirmDialog = false
                showSpinner()
                scope.launch {
                    when (meetingAttendeeViewModel.deleteMeetingAttendee()) {
                        DeleteMeetingAttendeeResult.Success -> {
                            showMessage(R.string.sukses, R.string.berhasil_hapus_attendance)
                            meetingAttendeeViewModel.getMeetingAttendees()
                        }
                        DeleteMeetingAttendeeResult.Error -> {
                            showMessage(R.string.error, R.string.network_error)
                        }
                    }
                }
            },
            onDismissRequest = { showConfirmDialog = false },
            message = R.string.hapus_attendance
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .padding(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxWidth()
                .padding(5.dp)
        ) {
            Card {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.daftar_kehadiran),
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                }
            }

            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(5.dp)
            ) {
                Spacer(modifier = Modifier.padding(5.dp))

                MeetingAttendeeList(
                    meetingAttendeeUiState = meetingAttendeeViewModel.meetingAttendeeUiState,
                    onDeleteClicked = { selectedUsername ->
                        meetingAttendeeViewModel.selectedUsername = selectedUsername
                        showConfirmDialog = true
                    }
                )
            }
        }
    }
}

@Composable
fun MeetingAttendeeList(
    meetingAttendeeUiState: MeetingAttendeeUiState,
    onDeleteClicked: (String) -> Unit = {}
) {
    when(meetingAttendeeUiState) {
        is MeetingAttendeeUiState.Error -> {
            Text(text = stringResource(id = R.string.error))
        }
        is MeetingAttendeeUiState.Loading -> {
            Text(text = "Loading")
        }
        is MeetingAttendeeUiState.Success -> {
            val attendees = meetingAttendeeUiState.attendees
            LazyColumn(modifier = Modifier.fillMaxHeight()) {
                items(items = attendees) { attendee ->
                    AttendeeCard(
                        attendee = attendee,
                        options = {
                            Column {
                                DrawerNavigationItem(
                                    icons = Icons.Filled.Delete,
                                    text = R.string.hapus_kehadiran,
                                    onClick = {
                                        attendee.username?.let { onDeleteClicked(it) }
                                    }
                                )
                            }
                        }
                    )
                }

            }
        }
    }
}