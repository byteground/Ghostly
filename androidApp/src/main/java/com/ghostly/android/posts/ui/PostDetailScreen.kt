package com.ghostly.android.posts.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import com.ghostly.android.R
import com.ghostly.android.posts.PostDetailViewModel
import com.ghostly.android.posts.getTimeFromStatus
import com.ghostly.android.ui.components.Toast
import com.ghostly.posts.models.Filter
import com.ghostly.posts.models.Post
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    post: Post,
    viewModel: PostDetailViewModel = koinViewModel(),
    onBackClick: () -> Unit,
    onEditClick: (Post) -> Unit,
) {
    val updatedPost by viewModel.observePost(post).collectAsState()

    val scope = rememberCoroutineScope()
    val filter = remember(updatedPost) {
        Filter.statusKeyToFilter(updatedPost?.status)
    }

    val time = remember(updatedPost) { updatedPost?.getTimeFromStatus() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { AppBarTitle(filter) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                },
                actions = {
                    ActionButtons(
                        updatedPost,
                        onEditClick = {
                            updatedPost?.let { post ->
                                onEditClick.invoke(post)
                            }
                        },
                        onPublishStatusChange = { filter ->
                            scope.launch {
                                viewModel.changePostStatus(filter)
                            }
                        }
                    )
                }
            )
        }
    ) { paddingValues ->
        PostDetailScreenContent(paddingValues, updatedPost, time)
    }
    val toastMessage by viewModel.toastMessage.collectAsState()
    Toast(toastMessage)
}

fun decodeFromNavigation(input: String): String {
    return URLDecoder.decode(input, StandardCharsets.UTF_8.toString())
}