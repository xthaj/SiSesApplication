package com.polstat.sisesapplication.ui.applicant

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.polstat.sisesapplication.R
import com.polstat.sisesapplication.ui.ConfirmDialog
import com.polstat.sisesapplication.ui.UserCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
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
                            showMessage(R.string.sukses, R.string.berhasil_hapus_akun_terpilih)
                            applicantViewModel.getAllApplicants()
                        }
                        DeclineApplicantResult.Error -> {
                            showMessage(R.string.error, R.string.network_error)
                        }
                    }
                }
            },
            onDismissRequest = { showConfirmDialog = false },
            message = R.string.hapus_akun
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
                            showMessage(R.string.sukses, R.string.logo)
                            applicantViewModel.getAllApplicants()
                        }
                        AcceptApplicantResult.Error -> {
                            showMessage(R.string.error, R.string.network_error)
                        }
                    }
                }
            },
            onDismissRequest = { confirmationForAccept = false },
            message = R.string.login // Modify the message for acceptance
        )
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
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Button(
                                        onClick = { applicant.username?.let {onDenyClicked(it)} },
                                    ) {
                                        Text(text = "Tolak")
                                    }
                                    Button(
                                        onClick = { applicant.username?.let {onAcceptClicked(it)} },
                                    ) {
                                        Text(text = "Terima", color = Color.White)
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