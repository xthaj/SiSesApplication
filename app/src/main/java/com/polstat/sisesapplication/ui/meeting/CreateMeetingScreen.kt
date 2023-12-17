package com.polstat.sisesapplication.ui.meeting

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.polstat.sisesapplication.R
import com.polstat.sisesapplication.ui.SiSesScreen
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMeetingScreen(
    createMeetingViewModel: CreateMeetingViewModel,
    modifier: Modifier = Modifier,
    showSpinner: () -> Unit = {},
    showMessage: (Int, Int) -> Unit = { _, _ -> },
    navController: NavHostController = rememberNavController()
) {
    val scope = rememberCoroutineScope()

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
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 32.dp,
                        bottom = 16.dp
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.buat_meeting),
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )

                    TextField(
                        value = createMeetingViewModel.meetingNameField,
                        onValueChange = { createMeetingViewModel.updateMeetingNameField(it) },
                        label = {
                            Text(text = stringResource(id = R.string.nama_meeting))
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.padding(8.dp))

                    TextField(
                        value = createMeetingViewModel.ruangField,
                        onValueChange = {
                            createMeetingViewModel.updateRuangField(it)
                        },
                        label = {
                            Text(text = stringResource(id = R.string.ruang_meeting))
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.padding(8.dp))

                    TextField(
                        value = createMeetingViewModel.meetingSummaryField,
                        onValueChange = { createMeetingViewModel.updateMeetingSummaryField(it) },
                        label = { Text(text = stringResource(id = R.string.ringkasan_meeting)) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.padding(8.dp))

                    Row {
                        Button(
                            onClick = {
                                showSpinner()

                                scope.launch {
                                    when (createMeetingViewModel.createMeeting()) {
                                        CreateMeetingDataResult.EmptyField -> showMessage(
                                            R.string.error,
                                            R.string.field_kosong
                                        )

                                        CreateMeetingDataResult.WrongRuangFormat -> showMessage(
                                            R.string.error,
                                            R.string.ruang_salah_format
                                        )

                                        CreateMeetingDataResult.Success -> showMessage(
                                            R.string.sukses,
                                            R.string.berhasil_ubah_profil
                                        )

                                        CreateMeetingDataResult.Error -> showMessage(
                                            R.string.error,
                                            R.string.network_error
                                        )
                                    }
                                }
                            }
                        ) {
                            Text(text = stringResource(id = R.string.buat_meeting))
                        }

                        Spacer(modifier = Modifier.padding(8.dp))

                        Button(
                            onClick = {
                                navController.navigate(SiSesScreen.MeetingManagement.name)
                            }
                        ) {
                            Text(text = stringResource(id = R.string.kembali))
                        }
                    }
                }
            }
        }
    }
}