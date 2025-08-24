package com.ghostly.android.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowOverflow
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ghostly.android.R
import com.ghostly.android.theme.AppTheme
import com.ghostly.android.theme.tagColors
import com.ghostly.posts.models.Tag

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Tags(
    modifier: Modifier,
    tags: List<Tag>,
    showCloseIcon: Boolean = false,
    onRemoveClick: ((Tag) -> Unit)? = null,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        maxLines = 2,
        overflow = FlowRowOverflow.Visible,
    ) {
        tags.forEach { tag ->
            val tagColor = remember(tag.name) { tagColors.random() }
            AssistChip(
                modifier = Modifier
                    .height(24.dp),
                onClick = { /*TODO*/ },
                label = {
                    Text(
                        text = tag.name,
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1
                    )
                },
                shape = MaterialTheme.shapes.large,
                colors = AssistChipDefaults.assistChipColors(
                    labelColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = tagColor,
                    trailingIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                border = AssistChipDefaults.assistChipBorder(
                    enabled = true,
                    borderColor = Color.Transparent
                ),
                trailingIcon = {
                    if (showCloseIcon) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.cd_remove_tag),
                            modifier = Modifier.clickable {
                                onRemoveClick?.invoke(tag)
                            }.padding(4.dp)
                        )
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TagsPreview() {
    AppTheme {
        Tags(
            modifier = Modifier,
            tags = listOf(
                Tag(id = "1", name = "Technology", slug = "technology"),
                Tag(id = "2", name = "Design", slug = "design"),
                Tag(id = "3", name = "Development", slug = "development"),
                Tag(id = "4", name = "Tutorial", slug = "tutorial")
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TagsWithCloseIconPreview() {
    AppTheme {
        Tags(
            modifier = Modifier,
            tags = listOf(
                Tag(id = "1", name = "Android", slug = "android"),
                Tag(id = "2", name = "Kotlin", slug = "kotlin"),
                Tag(id = "3", name = "Compose", slug = "compose")
            ),
            showCloseIcon = true,
            onRemoveClick = { /* Preview action */ }
        )
    }
}