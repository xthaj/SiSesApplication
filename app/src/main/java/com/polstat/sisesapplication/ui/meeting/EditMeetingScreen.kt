package com.polstat.sisesapplication.ui.meeting

import android.app.DatePickerDialog
import android.os.Build
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.polstat.sisesapplication.R
import com.polstat.sisesapplication.ui.ConfirmDialog
import com.polstat.sisesapplication.ui.PasswordTextField
import com.polstat.sisesapplication.ui.SiSesScreen
import com.polstat.sisesapplication.ui.profile.DeleteAccountResult
import com.polstat.sisesapplication.ui.profile.ProfileScreen
import com.polstat.sisesapplication.ui.profile.ProfileViewModel
import com.polstat.sisesapplication.ui.profile.UpdatePasswordResult
import com.polstat.sisesapplication.ui.profile.UpdateProfileResult
import com.polstat.sisesapplication.ui.theme.SiSesApplicationTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMeetingScreen(
    editMeetingViewModel: EditMeetingViewModel,
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
                        text = stringResource(id = R.string.edit_meeting),
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )

                    TextField(
                        value = editMeetingViewModel.meetingNameField,
                        onValueChange = { editMeetingViewModel.updateMeetingNameField(it) },
                        label = {
                            Text(text = stringResource(id = R.string.nama_meeting))
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.padding(8.dp))

                    TextField(
                        value = editMeetingViewModel.ruangField.toString(),// Convert the Int to String
                        onValueChange = { newValue ->
                            // Convert the String back to Int when the value changes
                            val intValue = newValue.toIntOrNull()
                                ?: 0 // Provide a default value if parsing fails
                            editMeetingViewModel.updateRuangField(intValue)
                        },
                        label = {
                            Text(text = stringResource(id = R.string.nama_meeting))
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.padding(8.dp))

                    DatePicker(
                        label = stringResource(id = R.string.tanggal_meeting),
                        value = editMeetingViewModel.meetingDateField,
                        onValueChange = { editMeetingViewModel.updateMeetingDateField(it) }
                    )

                    Spacer(modifier = Modifier.padding(8.dp))

                    TextField(
                        value = editMeetingViewModel.meetingSummaryField,
                        onValueChange = { editMeetingViewModel.updateMeetingSummaryField(it) },
                        label = { Text(text = stringResource(id = R.string.ringkasan_meeting)) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.padding(8.dp))


                    Row {
                        Button(
                            onClick = {
                                showSpinner()

                                scope.launch {
                                    when (editMeetingViewModel.updateMeetingData()) {
                                        UpdateMeetingDataResult.Success -> showMessage(
                                            R.string.sukses,
                                            R.string.berhasil_edit_meeting
                                        )

                                        UpdateMeetingDataResult.Error -> showMessage(
                                            R.string.error,
                                            R.string.network_error
                                        )
                                    }
                                }
                            }
                        ) {
                            Text(text = stringResource(id = R.string.edit_meeting))
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
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePicker(
    label: String,
    value: String,
    onValueChange: (String) -> Unit = {},
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    pattern: String = "yyyy-MM-dd",
) {
    val formatter = DateTimeFormatter.ofPattern(pattern)
    val date = if (value.isNotBlank()) LocalDate.parse(value, formatter) else LocalDate.now()
    val dialog = DatePickerDialog(
        LocalContext.current,
        { _, year, month, dayOfMonth ->
            onValueChange(LocalDate.of(year, month + 1, dayOfMonth).toString())
        },
        date.year,
        date.monthValue - 1,
        date.dayOfMonth,
    )

    TextField(
        label = {
            Text(text = label)
        },
        value = value,
        onValueChange = {},
        enabled = false,
        modifier = Modifier
            .clickable { dialog.show() }
            .fillMaxWidth(),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions
    )
}