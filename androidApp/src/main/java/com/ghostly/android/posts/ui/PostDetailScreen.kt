package com.ghostly.android.posts.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ghostly.android.R
import com.ghostly.android.posts.PostDetailViewModel
import com.ghostly.android.posts.PostsConstants
import com.ghostly.android.posts.getTimeFromStatus
import com.ghostly.android.ui.components.Tags
import com.ghostly.android.ui.components.Toast
import com.ghostly.posts.models.Filter
import com.ghostly.posts.models.Post
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText
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
                title = {
                    Row(
                        modifier = Modifier.height(54.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.posts),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "  /  ",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            modifier = Modifier,
                            text = filter.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.secondaryContainer
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            updatedPost?.let { post ->
                                onEditClick.invoke(post)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(R.string.cd_edit_post)
                        )
                    }

                    if (updatedPost?.status == Filter.Drafts.key) {
                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    viewModel.changePostStatus(Filter.Published)
                                }
                            },
                            modifier = Modifier
                                .wrapContentWidth()
                                .height(32.dp),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text(
                                text = stringResource(R.string.publish),
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    } else if (updatedPost?.status == Filter.Published.key) {
                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    viewModel.changePostStatus(Filter.Drafts)
                                }
                            },
                            modifier = Modifier
                                .wrapContentWidth()
                                .height(32.dp),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text(
                                text = stringResource(R.string.unpublish),
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .padding(bottom = 48.dp)
        ) {
            Text(
                text = updatedPost?.title ?: "",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Tags(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                tags = updatedPost?.tags ?: emptyList()
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .padding(end = 2.dp),
            ) {
                if (updatedPost?.authors?.isEmpty()?.not() == true) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(updatedPost?.featureImage.takeUnless { it.isNullOrEmpty() }
                                ?: PostsConstants.DEFAULT_PROFILE_IMAGE_URL)
                            .crossfade(true)
                            .build(),
                        modifier = Modifier
                            .padding(2.dp)
                            .aspectRatio(1f)
                            .clip(CircleShape),
                        contentDescription = stringResource(R.string.cd_author),
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .weight(1f)
                            .align(Alignment.CenterVertically),
                        text = updatedPost?.authors?.get(0)?.name ?: "",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight(100)),
                        maxLines = 1
                    )
                }
                Text(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = time ?: "",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(thickness = 1.dp)
            if (updatedPost?.featureImage.isNullOrEmpty().not()) {
                Spacer(modifier = Modifier.height(16.dp))
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(updatedPost?.featureImage)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.height(20.dp))

            val richTextState = rememberRichTextState()
            LaunchedEffect(updatedPost) {
                richTextState.setHtml(updatedPost?.content ?: "")
            }
            RichText(
                state = richTextState,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
    val toastMessage by viewModel.toastMessage.collectAsState()
    Toast(toastMessage)
}

fun decodeFromNavigation(input: String): String {
    return URLDecoder.decode(input, StandardCharsets.UTF_8.toString())
}