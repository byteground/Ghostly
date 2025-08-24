package com.ghostly.android.posts.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ghostly.android.R
import com.ghostly.posts.models.Filter
import com.ghostly.posts.models.Post

@Composable
fun AppBarTitle(
    filter: Filter,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.height(54.dp),
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
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun RowScope.ActionButtons(
    updatedPost: Post?,
    onEditClick: () -> Unit,
    onPublishStatusChange: (Filter) -> Unit,
) {
    IconButton(
        onClick = {
            onEditClick.invoke()
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
                onPublishStatusChange.invoke(Filter.Published)
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
                onPublishStatusChange.invoke(Filter.Drafts)
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