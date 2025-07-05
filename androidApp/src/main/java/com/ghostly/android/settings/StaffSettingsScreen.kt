package com.ghostly.android.settings

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.ghostly.android.R
import com.ghostly.android.ui.components.AccentButton
import com.ghostly.android.ui.components.AccentedExtendedFloatingActionButton
import com.ghostly.android.utils.isValidEmail
import com.ghostly.settings.models.Invite
import com.ghostly.settings.models.Role
import com.ghostly.settings.models.User
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun StaffSettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val showInviteDialog = remember { mutableStateOf(false) }
    val email = remember { mutableStateOf("") }

    val emailError = remember { mutableStateOf<String?>(null) }
    val isEmailTouched = remember { mutableStateOf(false) }

    val users by viewModel.users.collectAsState()

    val selectedRole = remember {
        mutableStateOf<Role?>(null)
    }

    LaunchedEffect(Unit) {
        viewModel.toastMessage.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(users) {
        if (selectedRole.value == null && users.isNotEmpty()) {
            selectedRole.value = users.keys.firstOrNull()
        }
    }

    Scaffold(
        topBar = {
            StaffScreenTopBar(navController = navController)
        },
        floatingActionButton = {
            AccentedExtendedFloatingActionButton(
                onClick = { showInviteDialog.value = true },
                shape = FloatingActionButtonDefaults.extendedFabShape,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.invite_people)
                )
                Text(text = "Invite People")
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) { paddingValues ->
        StaffListScreen(
            paddingValues = paddingValues,
            users = users,
        )
    }
    if (showInviteDialog.value) {
        InviteStaffDialog(
            modifier = Modifier,
            email = email,
            isEmailTouched = isEmailTouched,
            emailError = emailError,
            context = context,
            users = users,
            selectedRole = selectedRole,
            viewModel = viewModel,
            showInviteDialog = showInviteDialog
        )
    }
}

@Composable
fun InviteStaffDialog(
    modifier: Modifier = Modifier,
    email: MutableState<String>,
    isEmailTouched: MutableState<Boolean>,
    emailError: MutableState<String?>,
    context: Context,
    users: Map<Role, List<User>>,
    selectedRole: MutableState<Role?>,
    viewModel: SettingsViewModel,
    showInviteDialog: MutableState<Boolean>,
) {
    val scope = rememberCoroutineScope()

    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        onDismissRequest = { },
        title = { Text(stringResource(R.string.invite_a_new_staff_user)) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = email.value,
                    onValueChange = {
                        email.value = it
                        isEmailTouched.value = true
                        emailError.value = when {
                            it.isEmpty() -> context.getString(R.string.email_is_required)
                            !isValidEmail(it) -> context.getString(R.string.please_enter_a_valid_email_address)
                            else -> null
                        }
                    },
                    label = { Text(stringResource(R.string.email_address)) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = emailError.value != null && isEmailTouched.value,
                    supportingText = {
                        if (emailError.value != null && isEmailTouched.value) {
                            Text(
                                text = emailError.value ?: "",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    textStyle = MaterialTheme.typography.bodyLarge
                )

                Column {
                    users.keys.forEach { role ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedRole.value = role }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = selectedRole.value == role,
                                onClick = { selectedRole.value = role })
                            Text(
                                text = role.name.lowercase().replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Icon(
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .size(16.dp)
                                    .clickable { },
                                imageVector = Icons.AutoMirrored.Filled.Help,
                                contentDescription = ""
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            AccentButton(
                onClick = {
                    selectedRole.value?.let { role ->
                        scope.launch {
                            val success = viewModel.inviteStaff(
                                Invite(
                                    email = email.value, roleId = role.id
                                )
                            )
                            if (success) {
                                showInviteDialog.value = false
                                email.value = ""
                                isEmailTouched.value = false
                                emailError.value = null
                            }
                        }
                    }
                }, enabled = emailError.value == null && isEmailTouched.value
            ) {
                Text(stringResource(R.string.send_invitation))
            }
        },
        dismissButton = {
            TextButton(onClick = { showInviteDialog.value = false }) {
                Text(stringResource(R.string.cancel))
            }
        })
}