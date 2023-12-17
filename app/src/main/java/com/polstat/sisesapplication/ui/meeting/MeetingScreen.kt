package com.polstat.sisesapplication.ui.meeting

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.polstat.sisesapplication.R
import com.polstat.sisesapplication.ui.ConfirmDialog
import com.polstat.sisesapplication.ui.DrawerNavigationItem
import com.polstat.sisesapplication.ui.MeetingCard
import com.polstat.sisesapplication.ui.SiSesScreen
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MeetingScreen(
    isAdmin: Boolean,
    modifier: Modifier = Modifier,
    meetingViewModel: MeetingViewModel = viewModel(factory = MeetingViewModel.Factory),
    showSpinner: () -> Unit = {},
    showMessage: (Int, Int) -> Unit = { _, _ -> },
    navController: NavHostController = rememberNavController()
) {
    val scope = rememberCoroutineScope()
    var showConfirmDialog by rememberSaveable { mutableStateOf(false) }

    // For sorting and filtering
    var sortOrder by rememberSaveable { mutableStateOf(meetingViewModel.sort) }
    var startDate by rememberSaveable { mutableStateOf(meetingViewModel.startDate) }
    var endDate by rememberSaveable { mutableStateOf(meetingViewModel.endDate) }
    var expandedSortDropdown by rememberSaveable { mutableStateOf(false) }


    if (showConfirmDialog) {
        ConfirmDialog(
            onConfirmRequest = {
                showConfirmDialog = false
                showSpinner()
                scope.launch {
                    when (meetingViewModel.deleteMeeting()) {
                        DeleteMeetingResult.Success -> {
                            showMessage(R.string.sukses, R.string.berhasil_hapus_meeting)
                            meetingViewModel.getAllMeetings()
                        }
                        DeleteMeetingResult.Error -> {
                            showMessage(R.string.error, R.string.network_error)
                        }
                    }
                }
            },
            onDismissRequest = { showConfirmDialog = false },
            message = R.string.hapus_meeting
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxWidth()
                .padding(5.dp)
        ) {

            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Meeting",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp
                )

                if (isAdmin) {
                    Spacer(modifier = Modifier.width(12.dp))

                    IconButton(
                        onClick = { navController.navigate(SiSesScreen.CreateMeeting.name) },
                        // Adjust size of the IconButton to align with the text size
                        modifier = Modifier
                            .size(40.dp) // Example size, adjust as needed
                            .background(MaterialTheme.colorScheme.tertiary, shape = CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Create Meeting",
                            // Adjust icon size to fit well within the IconButton
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onTertiary
                        )
                    }
                }
            }


            Spacer(modifier = Modifier.padding(8.dp))

            Box {
                // Text styled like a button
                Text(
                    text = "Choose Sorting Order",
                    fontSize = 18.sp,
                    modifier = Modifier
                        .clickable { expandedSortDropdown = true }
                        .background(
                            MaterialTheme.colorScheme.tertiary,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(8.dp),
                    color = MaterialTheme.colorScheme.onTertiary,
                )

                // Dropdown menu positioned directly below the text
                DropdownMenu(
                    expanded = expandedSortDropdown,
                    onDismissRequest = { expandedSortDropdown = false },
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    DropdownMenuItem(
                        text = { Text(text = "Ascending") },
                        onClick = {
                            sortOrder = "asc"
                            expandedSortDropdown = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(text = "Descending") },
                        onClick = {
                            sortOrder = "desc"
                            expandedSortDropdown = false
                        }
                    )
                }
            }

            Column(modifier= Modifier.padding(25.dp)) {
                DatePicker("Start Date", startDate, onValueChange = { startDate = it })
                DatePicker("End Date", endDate, onValueChange = { endDate = it })
            }

            Row {
                // Buttons for applying and resetting filters
                Button(
                    onClick = { meetingViewModel.applyFilter(sortOrder, startDate, endDate) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    )
                )
                {
                    Text(text= "Apply Filters", color = MaterialTheme.colorScheme.onTertiary)
                }

                Spacer(modifier = Modifier.padding(8.dp))

                Button(
                    onClick = {
                        sortOrder = "desc"
                        startDate = ""
                        endDate = ""
                        meetingViewModel.applyFilter(sortOrder, startDate, endDate)
                },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    )) {
                    Text(text = "Reset Filters", color = MaterialTheme.colorScheme.onTertiary)
                }
            }

            Spacer(modifier = Modifier.padding(8.dp))

            MeetingList(
                isAdmin = isAdmin,
                meetingManagementUiState = meetingViewModel.meetingManagementUiState,
                onAttendClicked = { selectedMeetingId ->
                    scope.launch {
                        meetingViewModel.selectedMeetingId = selectedMeetingId
                        when (meetingViewModel.attendMeeting()) {
                            AttendMeetingResult.Success -> {
                                showMessage(R.string.sukses, R.string.berhasil_hadir_meeting)
                                meetingViewModel.getAllMeetings()
                            }

                            AttendMeetingResult.Error -> {
                                showMessage(R.string.error, R.string.network_error)
                            }
                        }
                    }
                },
                onAllAttendeesClicked = { meetingId ->
                    navController.navigate("${SiSesScreen.AttendeeMeeting.name}/$meetingId")
                },
                onDeleteClicked = { selectedMeetingId ->
                    meetingViewModel.selectedMeetingId = selectedMeetingId
                    showConfirmDialog = true
                },
                onEditClicked = { meetingId ->
                    navController.navigate("${SiSesScreen.EditMeeting.name}/$meetingId")
                }
            )
        }
    }
}

@Composable
fun MeetingList(
    isAdmin: Boolean,
    meetingManagementUiState: MeetingManagementUiState,
    onAttendClicked: (Int) -> Unit = {},
    onAllAttendeesClicked: (Int) -> Unit = {},
    onDeleteClicked: (Int) -> Unit = {},
    onEditClicked: (Int) -> Unit = {}
) {
    when(meetingManagementUiState) {
        is MeetingManagementUiState.Error -> {
            Text(text = stringResource(id = R.string.error))
        }
        is MeetingManagementUiState.Loading -> {
            Text(text = "Loading")
        }
        is MeetingManagementUiState.Success -> {
            val meetings = meetingManagementUiState.meetings
            LazyColumn(modifier = Modifier.fillMaxHeight()) {
                items(items = meetings) { meeting ->
                    MeetingCard(
                        title = meeting.meetingName,
                        meetingDate = meeting.meetingDate,
                        ruang = meeting.ruang,
                        meetingSummary = meeting.meetingSummary,
                        options = {
                            Column {
                                DrawerNavigationItem(
                                    icons = Icons.Filled.PersonAdd,
                                    text = R.string.buat_hadiri_pertemuan,
                                    onClick = {
                                        meeting.meetingId?.let { onAttendClicked(it) }
                                    }
                                )

                                if (isAdmin) {
                                    DrawerNavigationItem(
                                        icons = Icons.Filled.Person,
                                        text = R.string.kehadiran,
                                        onClick = {
                                            meeting.meetingId?.let { onAllAttendeesClicked(it) }
                                        }
                                    )

                                    DrawerNavigationItem(
                                        icons = Icons.Filled.Edit,
                                        text = R.string.edit_meeting,
                                        onClick = {
                                            meeting.meetingId?.let { onEditClicked(it) }
                                        }
                                    )

                                    DrawerNavigationItem(
                                        icons = Icons.Filled.Delete,
                                        text = R.string.hapus_meeting,
                                        onClick = {
                                            meeting.meetingId?.let { onDeleteClicked(it) }
                                        }
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}