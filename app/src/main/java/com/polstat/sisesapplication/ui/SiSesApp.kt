package com.polstat.sisesapplication.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.polstat.singadu.ui.LoginScreen
import com.polstat.sisesapplication.R
import com.polstat.sisesapplication.data.UserState
import com.polstat.sisesapplication.ui.home.HomeScreen
import com.polstat.sisesapplication.ui.meeting.MeetingScreen
import com.polstat.sisesapplication.ui.profile.ProfileScreen
import com.polstat.sisesapplication.ui.register.RegisterScreen
import kotlinx.coroutines.launch

enum class SiSesScreen {
    Login,
    Register,
    Home,
    Profile,
    ProblemTypeManagement,
    CreateProblemType,
    MeetingManagement,
    EditUser
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiSesApp(
    navController: NavHostController = rememberNavController(),
    siSesAppViewModel: SiSesAppViewModel = viewModel(factory = SiSesAppViewModel.Factory)
) {
    val loggedInUser = siSesAppViewModel.userState.collectAsState().value
    val uiState = siSesAppViewModel.uiState.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val showTopBar = when (navBackStackEntry?.destination?.route) {
        SiSesScreen.Login.name, SiSesScreen.Register.name -> false
        else -> true
    }
    val showFloatingActionButton = when (navBackStackEntry?.destination?.route) {
        SiSesScreen.ProblemTypeManagement.name -> true
        else -> false
    }
    val floatingActionButtonDestination = when (navBackStackEntry?.destination?.route) {
        SiSesScreen.ProblemTypeManagement.name -> SiSesScreen.CreateProblemType.name
        else -> ""
    }

    if (uiState.value.showProgressDialog) {
        ProgressDialog(onDismissRequest = { siSesAppViewModel.dismissSpinner() })
    }
    if (uiState.value.showMessageDialog) {
        MessageDialog(
            onDismissRequest = { siSesAppViewModel.dismissMessageDialog() },
            onClose = { siSesAppViewModel.dismissMessageDialog() },
            title = uiState.value.messageTitle,
            message = uiState.value.messageBody
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                SingaduDrawer(
                    user = loggedInUser,
                    navController = navController,
                    closeDrawer = {
                        scope.launch {
                            drawerState.apply {
                                close()
                            }
                        }
                    },
                    logout = { siSesAppViewModel.logout() }
                )
            }
        }
    ) {

        Scaffold(
            topBar = {
                if (showTopBar) {
                    SiSesAppBar(
                        onMenuClicked = {
                            scope.launch {
                                drawerState.apply {
                                    if (isClosed) open() else close()
                                }
                            }
                        }
                    )
                }
            },
            floatingActionButtonPosition = FabPosition.End,
            floatingActionButton = {
                if (showFloatingActionButton) {
                    FloatingActionButton(onClick = {
                        navController.navigate(floatingActionButtonDestination)
                    }) {
                        Text(
                            text = stringResource(id = R.string.plus),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = if (loggedInUser.token == "") SiSesScreen.Login.name else SiSesScreen.Home.name,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(route = SiSesScreen.Login.name) {
                    LoginScreen(
                        onLoginSuccess = {
                            // showProgressDialog = false
                            siSesAppViewModel.dismissSpinner()
                            navController.navigate(SiSesScreen.Home.name)
                        },
                        onRegisterButtonClicked = { navController.navigate(SiSesScreen.Register.name) },
                        showSpinner = { siSesAppViewModel.showSpinner() },
                        showMessage = { title, body -> siSesAppViewModel.showMessageDialog(title, body) }
                    )
                }

                composable(route = SiSesScreen.Register.name) {
                    RegisterScreen(
                        onBackButtonClicked = { navController.navigate(SiSesScreen.Login.name) },
                        showSpinner = { siSesAppViewModel.showSpinner() },
                        showMessage = { title, body -> siSesAppViewModel.showMessageDialog(title, body) }
                    )
                }

                composable(route = SiSesScreen.Home.name) {
                    HomeScreen()
                }

                composable(route = SiSesScreen.Profile.name) {
                    ProfileScreen(
                        username = loggedInUser.username,
                        showMessage = { title, body -> siSesAppViewModel.showMessageDialog(title, body) },
                        showSpinner = { siSesAppViewModel.showSpinner() },
                        navController = navController
                    )
                }

                composable(route = SiSesScreen.MeetingManagement.name) {
                    MeetingScreen(
                        showMessage = { title, body -> siSesAppViewModel.showMessageDialog(title, body) },
                        showSpinner = { siSesAppViewModel.showSpinner() },
                        navController = navController
                    )
                }

//                composable(
//                    route = "${SiSesScreen.EditUser.name}/{userId}",
//                    arguments = listOf(navArgument("userId") {
//                        type = NavType.LongType
//                    })
//                ) {
//                    EditUserScreen(
//                        editUserViewModel = viewModel(factory = EditUserViewModel.Factory),
//                        showSpinner = { singaduAppViewModel.showSpinner() },
//                        showMessage = { title, body -> singaduAppViewModel.showMessageDialog(title, body) }
//                    )
//                }
            }
        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiSesAppBar(
    modifier: Modifier = Modifier,
    onMenuClicked: () -> Unit = {}
) {

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = stringResource(id = R.string.logo),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.padding(6.dp))
                Text(text = stringResource(id = R.string.app_name))
            }
        },
        navigationIcon = {
            IconButton(onClick = onMenuClicked) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = stringResource(id = R.string.menu)
                )
            }
        },
        actions = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = stringResource(id = R.string.profile)
                )
            }
        },
        modifier = modifier
    )
}

@Composable
fun SingaduDrawer(
    user: UserState,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    closeDrawer: () -> Unit = {},
    logout: suspend () -> Unit = {}
) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier.fillMaxSize()
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = closeDrawer) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.kembali)
                )
            }

            Text(
                text = stringResource(id = R.string.menu)
            )
        }

        DrawerNavigationItem(
            Icons.Filled.Home,
            text = R.string.menu_beranda
        ) {
            navController.navigate(SiSesScreen.Home.name)
            closeDrawer()
        }

//        if (user.isAdmin) {
//            DrawerNavigationItem(
//                icons = Icons.Filled.MailOutline,
//                text = R.string.menu_manajemen_jenis_masalah
//            ) {
//                navController.navigate(SiSesScreen.ProblemTypeManagement.name)
//                closeDrawer()
//            }
//            DrawerNavigationItem(
//                Icons.Filled.Face,
//                text = R.string.menu_manajemen_user
//            ) {
//                navController.navigate(SiSesScreen.UserManagement.name)
//                closeDrawer()
//            }
//        }

        DrawerNavigationItem(
            icons = Icons.Filled.Face,
            text = R.string.menu_edit_profil
        ) {
            navController.navigate(SiSesScreen.Profile.name)
            closeDrawer()
        }

        DrawerNavigationItem(
            icons = Icons.Filled.ExitToApp,
            text = R.string.logout
        ) {
            scope.launch {
                logout()
                navController.navigate(SiSesScreen.Login.name)
                closeDrawer()
            }
        }

        DrawerNavigationItem(
            //TODO
            Icons.Filled.Home,
            text = R.string.menu_meeting
        ) {
            navController.navigate(SiSesScreen.MeetingManagement.name)
            closeDrawer()
        }
    }
}

@Composable
fun DrawerNavigationItem(
    icons: ImageVector,
    @StringRes text: Int,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Icon(
            icons,
            contentDescription = null
        )

        Spacer(modifier = Modifier.padding(4.dp))

        Text(
            text = stringResource(id = text)
        )
    }
}