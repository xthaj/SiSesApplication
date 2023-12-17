package com.polstat.sisesapplication.ui.applicant

import android.provider.ContactsContract.Profile
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.polstat.sisesapplication.R
import com.polstat.sisesapplication.ui.ConfirmDialog
import com.polstat.sisesapplication.ui.SiSesScreen
import com.polstat.sisesapplication.ui.theme.SiSesApplicationTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplyScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    applyViewModel: ApplyViewModel = viewModel(factory = ApplyViewModel.Factory),
    showMessage: (Int, Int) -> Unit = { _, _ -> },
    showSpinner: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    var expanded by remember { mutableStateOf(false) }

    // divisi
    val suggestions = applyViewModel.divisiOptions
    val selectedDivisi = applyViewModel.selectedDivisi
    var textfieldSize by remember { mutableStateOf(Size.Zero)}
    val icon = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown

    if (applyViewModel.showConfirmDialog) {
        ConfirmDialog(
            onConfirmRequest = {
                applyViewModel.showConfirmDialog = false
                showSpinner()

                scope.launch {
                    when (applyViewModel.apply()) {
                        ApplyResult.Success -> {
                            showMessage(R.string.sukses, R.string.berhasil_daftar)
                            navController.navigate(SiSesScreen.Profile.name)
                        }
                        else -> showMessage(R.string.error, R.string.network_error)
                    }
                }
            },
            onDismissRequest = { applyViewModel.showConfirmDialog = false },
            message = R.string.konfirmasi_hapus_akun
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
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 32.dp,
                        bottom = 16.dp
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.menu_pendaftaran),
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )

                    TextField(
                        value = applyViewModel.kelasField,
                        singleLine = true,
                        onValueChange = { applyViewModel.updateKelasField(it) },
                        label = {
                            Text(text = stringResource(id = R.string.kelas))
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        )
                    )

                    Spacer(modifier = Modifier.padding(8.dp))

                    Column (modifier= Modifier.padding(25.dp)){
                        OutlinedTextField(
                            value = selectedDivisi.value,
                            onValueChange = {
                                selectedDivisi.value = it
                                applyViewModel.updateDivisiField(it)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .onGloballyPositioned { coordinates ->
                                    textfieldSize = coordinates.size.toSize()
                                },
                            label = { Text("Select Divisi") },
                            trailingIcon = {
                                Icon(
                                    icon,
                                    "contentDescription",
                                    Modifier.clickable { expanded = !expanded })
                            }
                        )

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier
                                .width(with(LocalDensity.current) { textfieldSize.width.toDp() })
                        ) {
                            suggestions.forEach { label ->
                                DropdownMenuItem(
                                    text = { Text(text = label) },
                                    onClick = {
                                        selectedDivisi.value = label
                                        applyViewModel.updateDivisiField(label)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.padding(4.dp))

                    Button(
                        onClick = {
                            showSpinner()
                            scope.launch {
                                when (applyViewModel.apply()) {
                                    ApplyResult.AlreadyApplied -> showMessage(R.string.error,
                                        R.string.sudah_daftar)
                                    ApplyResult.Success -> {
                                        showMessage(R.string.sukses, R.string.berhasil_daftar)
                                        navController.navigate(SiSesScreen.Profile.name)
                                    }
                                    ApplyResult.Error -> showMessage(
                                        R.string.error,
                                        R.string.network_error
                                    )
                                }
                            }
                        }
                    ) {
                        Text(text = stringResource(id = R.string.daftar_ses))
                    }

                    Spacer(modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    val username = "Testing@gmail.com"

    SiSesApplicationTheme {
        ApplyScreen(
//            username = username,
            navController = rememberNavController()
        )
    }
}