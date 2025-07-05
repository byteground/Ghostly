package com.ghostly.android.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.SupervisedUserCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ghostly.android.R
import com.ghostly.android.login.LoginViewModel
import com.ghostly.android.ui.components.versionName
import com.ghostly.android.utils.capitalize
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel(),
    loginViewModel: LoginViewModel = koinViewModel(),
    onLogout: () -> Unit,
    onStaffSettingsClicked: () -> Unit,
) {
    val storeName by viewModel.storeName.collectAsState()
    val storeIcon by viewModel.storeIcon.collectAsState()

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val showLogoutDialog = remember { mutableStateOf(false) }
    val showConfirmLogoutDialog = remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.Top
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(storeIcon)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .heightIn(max = 120.dp),
                contentScale = ContentScale.Inside
            )
            Spacer(modifier = Modifier.height(8.dp))
            storeName?.capitalize()?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            Spacer(modifier = Modifier.height(80.dp))
            PreferenceItem(
                icon = Icons.Filled.SupervisedUserCircle,
                name = stringResource(R.string.manage_staff)
            ) {
                onStaffSettingsClicked.invoke()
            }
            PreferenceItem(
                icon = Icons.AutoMirrored.Filled.Logout,
                name = stringResource(R.string.pref_logout)
            ) {
                showConfirmLogoutDialog.value = true
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.app_version, context.versionName()),
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
        LogoutDialog(showLogoutDialog)
        ConfirmLogout(showConfirmLogoutDialog) {
            scope.launch {
                showConfirmLogoutDialog.value = false
                showLogoutDialog.value = true
                viewModel.clearLogin()
                loginViewModel.reset()
                showLogoutDialog.value = false
                onLogout.invoke()
            }
        }
    }
}

@Composable
fun LogoutDialog(shouldShowDialog: MutableState<Boolean>) {
    if (shouldShowDialog.value) {
        Dialog(onDismissRequest = { }) {
            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.large)
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                ) {
                    Text(
                        text = stringResource(R.string.cleaning_slate),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                    Spacer(modifier = Modifier.height(40.dp))
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(35.dp)
                            .align(Alignment.CenterHorizontally),
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }
    }
}


@Composable
fun ConfirmLogout(
    shouldShowDialog: MutableState<Boolean>,
    confirmLogout: () -> Unit,
) {
    if (shouldShowDialog.value) {
        AlertDialog(
            onDismissRequest = {
                shouldShowDialog.value = false
            },
            title = { Text(text = stringResource(R.string.confirm_logout)) },
            text = { Text(text = stringResource(R.string.are_you_sure_you_want_to_logout)) },
            confirmButton = {
                Button(onClick = { confirmLogout.invoke() }) {
                    Text(
                        text = stringResource(R.string.confirm_logout),
                        color = Color.White
                    )
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        shouldShowDialog.value = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.Black,
                        disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        disabledContentColor = MaterialTheme.colorScheme.inverseSurface
                    ),
                ) {
                    Text(
                        text = stringResource(R.string.cancel),
                    )
                }
            }
        )
    }
}