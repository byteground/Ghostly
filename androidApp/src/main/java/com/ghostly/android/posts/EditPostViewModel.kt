package com.ghostly.android.posts

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ghostly.network.models.Result
import com.ghostly.posts.data.PostDataSource
import com.ghostly.posts.data.PostRepository
import com.ghostly.posts.models.Post
import com.ghostly.posts.models.Tag
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class EditPostUiState {
    data object Idle : EditPostUiState()
    data object HasChanges : EditPostUiState()
    data object Success : EditPostUiState()
    data class Error(val message: String) : EditPostUiState()
}

class EditPostViewModel(
    private val postRepository: PostRepository, private val postDataSource: PostDataSource,
) : ViewModel() {

    private val _post = MutableStateFlow<Post?>(null)
    val post: StateFlow<Post?> = _post.asStateFlow()

    private val _uiState = MutableStateFlow<EditPostUiState>(EditPostUiState.Idle)
    val uiState: StateFlow<EditPostUiState> = _uiState.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving = _isSaving.asStateFlow()

    fun initializePost(post: Post) {
        _post.value = post
    }

    fun updateTitle(newTitle: String) {
        val currentPost = _post.value
        if (currentPost != null) {
            val updatedPost = currentPost.copy(title = newTitle)
            _post.value = updatedPost
            _uiState.value = EditPostUiState.HasChanges
        }
    }

    fun addTag(tagName: String) {
        val currentPost = _post.value ?: return

        val updatedTags = currentPost.tags.toMutableList()

        if (!updatedTags.any { it.name.equals(tagName, ignoreCase = true) }) {
            val newTag = Tag(
                name = tagName.trim(), slug = tagName.trim().lowercase().replace(" ", "-")
            )
            updatedTags.add(newTag)
            val updatedPost = currentPost.copy(tags = updatedTags)
            _post.value = updatedPost
            _uiState.value = EditPostUiState.HasChanges
        } else {
            Log.w("EditPostViewModel", "Tag '$tagName' already exists, skipping")
        }
    }

    fun removeTag(tag: Tag) {
        val currentPost = _post.value ?: return

        val updatedTags = currentPost.tags.toMutableList()
        updatedTags.remove(tag)
        val updatedPost = currentPost.copy(tags = updatedTags)
        _post.value = updatedPost
        _uiState.value = EditPostUiState.HasChanges
    }

    fun savePost() {
        if (_isSaving.value) return

        viewModelScope.launch {
            _isSaving.value = true
            val post = _post.value ?: return@launch

            val result = postRepository.updatePost(post)

            _uiState.value = when (result) {
                is Result.Success -> {

                    when (val refreshResult = postRepository.refreshPostFromServer(post.id)) {
                        is Result.Success -> {
                            val refreshedPost = refreshResult.data
                            if (refreshedPost != null) {
                                _post.value = refreshedPost
                                postDataSource.updatePost(refreshedPost)
                            } else {
                                Log.e("EditPostViewModel", "Server returned null post data")
                            }
                            EditPostUiState.Success
                        }

                        is Result.Error -> {
                            Log.e(
                                "EditPostViewModel",
                                "Failed to refresh post from server: ${refreshResult.message}"
                            )
                            EditPostUiState.Error(refreshResult.message ?: "Updating Failed")
                        }
                    }
                }

                is Result.Error -> {
                    Log.e("EditPostViewModel", "Server update failed: ${result.message}")
                    EditPostUiState.Error(result.message ?: "Unknown error")
                }
            }
            _isSaving.value = false
        }
    }
} 