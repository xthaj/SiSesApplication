package com.polstat.sisesapplication.ui.meeting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.polstat.sisesapplication.R
import com.polstat.sisesapplication.ui.ConfirmDialog
import com.polstat.sisesapplication.ui.DrawerNavigationItem
import com.polstat.sisesapplication.ui.ItemCard
import com.polstat.sisesapplication.ui.MeetingCard
import com.polstat.sisesapplication.ui.SiSesScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
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

    if (showConfirmDialog) {
        ConfirmDialog(
            onConfirmRequest = {
                showConfirmDialog = false
                showSpinner()
                scope.launch {
//                    when (userManagementViewModel.deleteUser()) {
//                        DeleteUserResult.Success -> {
//                            showMessage(R.string.sukses, R.string.berhasil_hapus_akun_terpilih)
//                            userManagementViewModel.getAllUsers()
//                            userManagementViewModel.filterUsers(userManagementViewModel.searchQuery)
//                        }
//                        DeleteUserResult.Error -> {
//                            showMessage(R.string.error, R.string.network_error)
//                        }
//                    }
                }
            },
            onDismissRequest = { showConfirmDialog = false },
            message = R.string.hapus_akun
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
//        TextField(
//            value = userManagementViewModel.searchQuery,
//            onValueChange = { userManagementViewModel.filterUsers(it) },
//            singleLine = true,
//            placeholder = {
//                Text(text = stringResource(R.string.cari))
//            },
//            keyboardOptions = KeyboardOptions.Default.copy(
//                imeAction = ImeAction.Done
//            ),
//            modifier = Modifier.fillMaxWidth()
//        )

        Spacer(modifier = Modifier.padding(5.dp))

        MeetingList(
            isAdmin = isAdmin,
            meetingManagementUiState = meetingViewModel.meetingManagementUiState,
            onDeleteClicked = {
//                    selectedUserId ->
//                userManagementViewModel.selectedUserId = selectedUserId
//                showConfirmDialog = true
            },
            onEditClicked = {
                    userId ->
                navController.navigate("${SiSesScreen.EditUser.name}/$userId")
            }
        )
    }
}

@Composable
fun MeetingList(
    isAdmin: Boolean,
    meetingManagementUiState: MeetingManagementUiState,
    onDeleteClicked: (Long) -> Unit = {},
    onEditClicked: (Long) -> Unit = {}
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
                                    icons = Icons.Filled.Edit,
                                    text = R.string.edit_user,
                                    onClick = {
//                                        meeting.meetingId?.let { onEditClicked(it) }
                                    }
                                )

                                if (isAdmin) {
                                    DrawerNavigationItem(
                                        icons = Icons.Filled.Delete,
                                        text = R.string.hapus_akun,
                                        onClick = {
//                                        user.id?.let { onDeleteClicked(it) }
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