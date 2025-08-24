package com.ghostly.android.posts.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.ghostly.android.posts.PostsConstants
import com.ghostly.android.ui.components.HtmlWebView
import com.ghostly.android.ui.components.Tags
import com.ghostly.posts.models.Post

@Composable
fun PostDetailScreenContent(
    paddingValues: PaddingValues,
    updatedPost: Post?,
    time: String?,
) {
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

        HtmlWebView(
            htmlContent = updatedPost?.content ?: "",
            modifier = Modifier.fillMaxWidth()
        )
    }
}