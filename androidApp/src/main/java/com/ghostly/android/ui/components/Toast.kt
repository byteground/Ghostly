package com.ghostly.android.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.ghostly.android.R
import com.ghostly.posts.models.PostUiMessage

@Composable
fun Toast(message: PostUiMessage?) {
    val context = LocalContext.current

    val messageTextId = remember(message) {
        when (message) {
            PostUiMessage.PublishingSuccessful -> R.string.published_successfully
            PostUiMessage.PublishingFailed -> R.string.publishing_failed
            PostUiMessage.PublishingFailedUnauthorized -> R.string.publishing_failed_unauthorized
            PostUiMessage.UnpublishingSuccessful -> R.string.unpublished_successfully
            PostUiMessage.UnpublishingFailed -> R.string.unpublishing_failed
            PostUiMessage.UnpublishingFailedUnauthorized -> R.string.unpublishing_failed_unauthorized
            else -> -1
        }
    }

    LaunchedEffect(message) {
        if (messageTextId != -1) {
            context.toast(messageTextId)
        }
    }
}