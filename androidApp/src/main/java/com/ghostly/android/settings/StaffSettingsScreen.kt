package com.ghostly.android.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffSettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var showInviteDialog by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }

    var emailError by remember { mutableStateOf<String?>(null) }
    var isEmailTouched by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchUsers()
        viewModel.fetchRoles()
    }

    val users by viewModel.users.collectAsState()
    val roles by viewModel.roles.collectAsState()

    var selectedRole by remember {
        mutableStateOf<Role?>(null)
    }

    LaunchedEffect(roles) {
        if (selectedRole == null) {
            selectedRole = roles.firstOrNull()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.height(54.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.staff),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            AccentedExtendedFloatingActionButton(
                onClick = { showInviteDialog = true },
                shape = FloatingActionButtonDefaults.extendedFabShape,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Invite People"
                )

                Text(text = "Invite People")
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) { paddingValues ->

        if (users.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Text(
                    text = "No users yet",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        } else {
            UsersScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                roles = roles,
                users = users,
            )
        }

    }
    if (showInviteDialog) {
        AlertDialog(
            properties = DialogProperties(usePlatformDefaultWidth = false),
            modifier = Modifier
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
                        value = email,
                        onValueChange = {
                            email = it
                            isEmailTouched = true
                            emailError = when {
                                it.isEmpty() -> context.getString(R.string.email_is_required)
                                !isValidEmail(it) -> context.getString(R.string.please_enter_a_valid_email_address)
                                else -> null
                            }
                        },
                        label = { Text(stringResource(R.string.email_address)) },
                        modifier = Modifier.fillMaxWidth(),
                        isError = emailError != null && isEmailTouched,
                        supportingText = {
                            if (emailError != null && isEmailTouched) {
                                Text(
                                    text = emailError ?: "",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        textStyle = MaterialTheme.typography.bodyLarge
                    )

                    Column {
                        roles.forEach { role ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedRole = role }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                RadioButton(
                                    selected = selectedRole == role,
                                    onClick = { selectedRole = role }
                                )
                                Text(
                                    text = role.name.lowercase()
                                        .replaceFirstChar { it.uppercase() },
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
                        selectedRole?.let {
                            scope.launch {
                                viewModel.inviteStaff(
                                    Invite(
                                        email = email,
                                        roleId = it.id
                                    )
                                )
                                showInviteDialog = false
                            }
                        }
                    },
                    enabled = emailError == null && isEmailTouched
                ) {
                    Text(stringResource(R.string.send_invitation))
                }
            },
            dismissButton = {
                TextButton(onClick = { showInviteDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}


@Composable
fun UserCard(
    modifier: Modifier = Modifier,
    user: User,
    role: Role,
    onRevoke: () -> Unit = {},
    onResend: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CircleAvatar(
                text = user.email.first().toString(),
                backgroundColor = MaterialTheme.colorScheme.primary
            )

            Column {
                Text(user.email)
                Text(
                    text = role.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(
                onClick = onRevoke,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Revoke")
            }

            TextButton(
                onClick = onResend,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Resend")
            }
        }
    }
}

@Composable
private fun CircleAvatar(
    text: String,
    backgroundColor: Color,
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.uppercase(),
            color = Color.White,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Preview
@Composable
private fun PreviewUserCard() {
    UserCard(
        user = User(
            "1",
            "User 1",
            "user@example.com",
            listOf(Role("1", "Contributor", "People who contribute"))
        ),
        role = Role("1", "Contributor", "People who contribute"),
    )
}

@Composable
fun UsersScreen(
    modifier: Modifier = Modifier,
    roles: List<Role>,
    users: Map<Role, List<User>>,
) {
    val pages = roles.map { it.name.replaceFirstChar { it.uppercase() } }
    val pagerState = rememberPagerState(pageCount = { roles.size })
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            edgePadding = 0.dp,
        ) {
            pages.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scope.launch {
                            pagerState.scrollToPage(index)
                        }
                    },
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
                    content = {
                        Text(text = title)
                    }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            pageSpacing = 8.dp,
            modifier = Modifier.weight(1f)
        ) { page ->
            users[roles[page]]?.let { users ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(users) { _, user ->
                        UserCard(
                            user = user,
                            role = roles[page],
                        )
                    }
                }
            } ?: Box(Modifier.fillMaxSize()) {
                Text(text = "No users")
            }

        }
    }
}

internal enum class StaffRole {
    CONTRIBUTOR,
    AUTHOR,
    EDITOR,
    ADMINISTRATOR
}

@Composable
private fun getRoleDescription(role: StaffRole): String {
    return when (role) {
        StaffRole.CONTRIBUTOR -> "Can create and edit their own posts, but cannot publish. An Editor needs to approve and publish for them."
        StaffRole.AUTHOR -> "A trusted user who can create, edit and publish their own posts, but can't modify others."
        StaffRole.EDITOR -> "Can invite and manage other Authors and Contributors, as well as edit and publish any posts on the site."
        StaffRole.ADMINISTRATOR -> "Trusted staff user who should be able to manage all content and users, as well as site settings and options."
    }
}