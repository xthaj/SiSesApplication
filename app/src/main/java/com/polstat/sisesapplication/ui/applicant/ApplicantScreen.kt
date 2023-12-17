package com.polstat.sisesapplication.ui.applicant

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.polstat.sisesapplication.R
import com.polstat.sisesapplication.ui.ConfirmDialog
import com.polstat.sisesapplication.ui.UserCard
import kotlinx.coroutines.launch

@Composable
fun ApplicantScreen(
    modifier: Modifier = Modifier,
    applicantViewModel: ApplicantViewModel = viewModel(factory = ApplicantViewModel.Factory),
    showSpinner: () -> Unit = {},
    showMessage: (Int, Int) -> Unit = { _, _ -> },
    navController: NavHostController = rememberNavController()
) {
    val scope = rememberCoroutineScope()
    var showConfirmDialog by rememberSaveable { mutableStateOf(false) }
    var confirmationForAccept by rememberSaveable { mutableStateOf(false) }

    if (showConfirmDialog) {
        ConfirmDialog(
            onConfirmRequest = {
                showConfirmDialog = false
                showSpinner()
                scope.launch {
                    when (applicantViewModel.declineApplicant()) {
                        DeclineApplicantResult.Success -> {
                            showMessage(R.string.sukses, R.string.berhasil_menolak_pendaftar)
                            applicantViewModel.getAllApplicants()
                        }
                        DeclineApplicantResult.Error -> {
                            showMessage(R.string.error, R.string.network_error)
                        }
                    }
                }
            },
            onDismissRequest = { showConfirmDialog = false },
            message = R.string.tolak_pendaftar
        )
    }

    if (confirmationForAccept) {
        ConfirmDialog(
            onConfirmRequest = {
                confirmationForAccept = false
                showSpinner()
                scope.launch {
                    when (applicantViewModel.acceptApplicant()) { // Modify to call accept method
                        AcceptApplicantResult.Success -> {
                            showMessage(R.string.sukses, R.string.berhasil_menerima_pendaftar)
                            applicantViewModel.getAllApplicants()
                        }
                        AcceptApplicantResult.Error -> {
                            showMessage(R.string.error, R.string.network_error)
                        }
                    }
                }
            },
            onDismissRequest = { confirmationForAccept = false },
            message = R.string.terima_pendaftar // Modify the message for acceptance
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
                        text = stringResource(id = R.string.pendaftar),
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

                ApplicantList(
                    applicantsManagementUiState = applicantViewModel.applicantsManagementUiState,
                    onDenyClicked = { selectedUsername ->
                        applicantViewModel.selectedUsername = selectedUsername
                        showConfirmDialog = true
                    },
                    onAcceptClicked = { selectedUsername ->
                        applicantViewModel.selectedUsername = selectedUsername
                        confirmationForAccept = true
                    }
                )
            }
        }
    }
}

@Composable
fun ApplicantList(
    applicantsManagementUiState: ApplicantsManagementUiState,
    onDenyClicked: (String) -> Unit = {},
    onAcceptClicked: (String) -> Unit = {}
) {
    when(applicantsManagementUiState) {
        is ApplicantsManagementUiState.Error -> {
            Text(text = stringResource(id = R.string.error))
        }
        is ApplicantsManagementUiState.Loading -> {
            Text(text = "Loading")
        }
        is ApplicantsManagementUiState.Success -> {
            val applicants = applicantsManagementUiState.applicants
            LazyColumn(modifier = Modifier.fillMaxHeight()) {
                items(items = applicants) { applicant ->
                    UserCard(
                        user = applicant,
                        options = {
                            Spacer(modifier = Modifier.padding(8.dp))

                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Button(
                                        onClick = { applicant.username?.let {onDenyClicked(it)} },
                                    ) {
                                        Text(text = "Tolak")
                                    }
                                    Spacer(modifier = Modifier.padding(8.dp))

                                    Button(
                                        onClick = { applicant.username?.let {onAcceptClicked(it)} },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.tertiary
                                        )
                                    ) {
                                        Text(text = "Terima", color=MaterialTheme.colorScheme.onTertiary)
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}