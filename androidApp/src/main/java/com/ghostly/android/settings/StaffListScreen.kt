package com.ghostly.android.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ghostly.settings.models.Role
import com.ghostly.settings.models.StaffRole
import com.ghostly.settings.models.User
import kotlinx.coroutines.launch

@Composable
fun StaffListScreen(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    users: Map<Role, List<User>>
) {
    val isLoading = remember { mutableStateOf(true) }

    LaunchedEffect(users) {
        if (users.isNotEmpty()) {
            isLoading.value = false
        }
    }

    when {
        isLoading.value -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Text(
                        text = "Loading staff members...",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }

        users.isEmpty() -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No users yet",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        else -> {
            UsersScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                roles = users.keys.toList(),
                users = users,
            )
        }
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

@Preview(showBackground = true)
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
            users[roles[page]]?.takeIf { it.isNotEmpty() }?.let { users ->
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
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "No users",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
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