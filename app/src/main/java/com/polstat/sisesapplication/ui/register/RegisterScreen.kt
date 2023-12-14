package com.polstat.sisesapplication.ui.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.polstat.sisesapplication.R
import com.polstat.sisesapplication.ui.PasswordTextField
import com.polstat.sisesapplication.ui.theme.SiSesApplicationTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    onBackButtonClicked: () -> Unit = {},
    registerViewModel: RegisterViewModel = viewModel(factory = RegisterViewModel.Factory),
    showSpinner: () -> Unit = {},
    showMessage: (Int, Int) -> Unit = { _, _ -> }
) {
    val coroutineScope = rememberCoroutineScope()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Card(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = modifier.padding(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 16.dp)
            ) {

                Image(painter = painterResource(
                    id = R.drawable.logo),
                    contentDescription = stringResource(id = R.string.logo),
                    modifier = Modifier.size(128.dp)
                )

                Spacer(modifier = Modifier.padding(8.dp))

                Text(
                    text = stringResource(id = R.string.register),
                    style = TextStyle(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 26.sp
                    )
                )

                Spacer(modifier = Modifier.padding(12.dp))

                TextField(
                    value = registerViewModel.usernameField,
                    onValueChange = { registerViewModel.updateUsernameField(it) },
                    placeholder = { Text(text = stringResource(id = R.string.username)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Email
                    )
                )

                TextField(
                    value = registerViewModel.nameField,
                    onValueChange = { registerViewModel.updateNameField(it) },
                    placeholder = { Text(text = stringResource(id = R.string.nama)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
                )

                PasswordTextField(
                    value = registerViewModel.passwordField,
                    onValueChange = { registerViewModel.updatePasswordField(it) },
                    placeholder = { Text(text = stringResource(id = R.string.password)) },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Password
                    )
                )

                PasswordTextField(
                    value = registerViewModel.confirmPasswordField,
                    onValueChange = { registerViewModel.updateConfirmPasswordField(it) },
                    placeholder = { Text(text = stringResource(id = R.string.konfirmasi_password)) },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Password
                    )
                )

                Spacer(modifier = Modifier.padding(top = 24.dp))

                Button(
                    onClick = {
                        showSpinner()
                        coroutineScope.launch {
                            when (registerViewModel.register()) {
                                RegisterResult.Success -> showMessage(R.string.sukses, R.string.berhasil_buat_akun)
                                RegisterResult.EmptyField -> showMessage(R.string.error, R.string.semua_field_harus_diisi)
                                RegisterResult.PasswordMismatch -> showMessage(R.string.error, R.string.password_mismatch)
                                else -> showMessage(R.string.error, R.string.network_error)
                            }
                        }
                    }
                ) {
                    Text(text = stringResource(id = R.string.register))
                }

                Spacer(modifier = Modifier.padding(8.dp))

                Text(
                    text = stringResource(id = R.string.kembali),
                    style = TextStyle(
                        fontWeight = FontWeight.Light,
                        fontSize = 14.sp,
                        textDecoration = TextDecoration.Underline,
                        color = MaterialTheme.colorScheme.secondary
                    ),
                    modifier = Modifier.clickable { onBackButtonClicked() }
                )
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun RegisterScreenPreview() {
    SiSesApplicationTheme {
        RegisterScreen()
    }
}