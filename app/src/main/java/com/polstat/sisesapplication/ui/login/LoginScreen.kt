package com.polstat.sisesapplication.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.style.TextAlign
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
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    onRegisterButtonClicked: () -> Unit = {},
    loginViewModel: LoginViewModel = viewModel(factory = LoginViewModel.Factory),
    showSpinner: () -> Unit = {},
    showMessage: (Int, Int) -> Unit = { _, _ -> }
) {
    val coroutineScope = rememberCoroutineScope()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Card {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = modifier.padding(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = stringResource(id = R.string.logo),
                    modifier = Modifier.size(128.dp)
                )

                Text(
                    text = stringResource(id = R.string.app_name_full),
                    style = TextStyle(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 26.sp,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.widthIn(0.dp, 240.dp)
                )
                
                Spacer(modifier = Modifier.padding(12.dp))

                TextField(
                    value = loginViewModel.usernameField,
                    onValueChange = { loginViewModel.updateUsername(it) },
                    placeholder = { Text(text = stringResource(id = R.string.username)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    )
                )

                PasswordTextField(
                    value = loginViewModel.passwordField,
                    onValueChange = { loginViewModel.updatePassword(it) },
                    placeholder = { Text(text = stringResource(id = R.string.password)) },
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
                            when(loginViewModel.attemptLogin()) {
                                LoginResult.Success -> onLoginSuccess()
                                LoginResult.WrongEmailOrPassword -> showMessage(R.string.error, R.string.email_atau_password_salah)
                                LoginResult.BadInput -> showMessage(R.string.error, R.string.semua_field_harus_diisi)
                                else -> showMessage(R.string.error, R.string.network_error)
                            }
                        }
                    }
                ) {
                    Text(text = stringResource(id = R.string.login))
                }

                Spacer(modifier = Modifier.padding(8.dp))

                Text(
                    text = stringResource(id = R.string.link_daftar),
                    style = TextStyle(
                        fontWeight = FontWeight.Light,
                        fontSize = 14.sp,
                        textDecoration = TextDecoration.Underline,
                        color = MaterialTheme.colorScheme.secondary
                    ),
                    modifier = Modifier.clickable { onRegisterButtonClicked() }
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    SiSesApplicationTheme {
        LoginScreen(
            onLoginSuccess = {}
        )
    }
}