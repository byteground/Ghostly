package com.ghostly.android.posts.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ghostly.android.R
import com.ghostly.android.posts.EditPostUiState
import com.ghostly.android.posts.EditPostViewModel
import com.ghostly.android.theme.AppTheme
import com.ghostly.android.ui.components.Tags
import com.ghostly.posts.models.Post
import com.ghostly.posts.models.Tag
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPostScreen(
    post: Post,
    viewModel: EditPostViewModel = koinViewModel(),
    onEditSuccess: () -> Unit,
    onBackClick: () -> Unit,
) {
    val currentPost by viewModel.post.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    var tagInput by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(post) {
        viewModel.initializePost(post)
    }

    LaunchedEffect(uiState) {
        val state = uiState
        if (state is EditPostUiState.Success) {
            onEditSuccess.invoke()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.edit_post),
                        style = MaterialTheme.typography.titleMedium
                    )
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
                    SaveButton(uiState, isSaving) {
                        viewModel.savePost()
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
            OutlinedTextField(
                value = currentPost?.title ?: "",
                onValueChange = {
                    viewModel.updateTitle(it)
                },
                label = { Text(stringResource(R.string.title)) },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                singleLine = false,
                textStyle = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(24.dp))

            TagsSection(
                tags = currentPost?.tags ?: emptyList(),
                tagInput = tagInput,
                onTagInputChange = { tagInput = it },
                onAddTag = {
                    viewModel.addTag(it)
                    tagInput = ""
                },
                onRemoveTag = {
                    viewModel.removeTag(it)
                },
                focusRequester = focusRequester
            )

            Spacer(modifier = Modifier.height(24.dp))

        }
    }
}

@Composable
fun TagsSection(
    tags: List<Tag>,
    tagInput: String,
    onTagInputChange: (String) -> Unit,
    onAddTag: (String) -> Unit,
    onRemoveTag: (Tag) -> Unit,
    focusRequester: FocusRequester,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.tags),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        if (tags.isNotEmpty()) {
            Tags(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                tags = tags,
                showCloseIcon = true,
                onRemoveClick = onRemoveTag
            )
        }

        OutlinedTextField(
            value = tagInput,
            onValueChange = onTagInputChange,
            label = { Text(stringResource(R.string.add_tag)) },
            placeholder = { Text(stringResource(R.string.press_enter_to_add)) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .focusRequester(focusRequester),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                onDone = {
                    if (tagInput.isNotBlank()) {
                        onAddTag(tagInput.trim())
                    }
                }
            )
        )
    }
}

@Composable
fun SaveButton(
    uiState: EditPostUiState,
    isSaving: Boolean,
    onSavePostClicked: () -> Unit,
) {
    OutlinedButton(
        onClick = { onSavePostClicked.invoke() },
        modifier = Modifier
            .wrapContentWidth()
            .height(32.dp),
        shape = MaterialTheme.shapes.medium,
        enabled = uiState == EditPostUiState.HasChanges,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = MaterialTheme.colorScheme.background
        )
    ) {
        if (isSaving) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = stringResource(R.string.save),
                style = MaterialTheme.typography.titleSmall,
                color = LocalContentColor.current
            )
        }
    }
}

@Preview(name = "Idle State")
@Composable
private fun PreviewSaveButtonIdle() {
    AppTheme {
        SaveButton(EditPostUiState.Idle, false) {}
    }
}

@Preview(name = "Saving State")
@Composable
private fun PreviewSaveButtonSaving() {
    AppTheme {
        SaveButton(EditPostUiState.HasChanges, true) {}
    }
}

@Preview(name = "Has Changes State")
@Composable
private fun PreviewSaveButtonHasChanges() {
    AppTheme {
        SaveButton(EditPostUiState.HasChanges, false) {}
    }
}


