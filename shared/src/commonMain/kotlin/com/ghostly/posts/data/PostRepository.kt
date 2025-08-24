package com.ghostly.posts.data

import androidx.paging.ExperimentalPagingApi
import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import com.ghostly.database.dao.PostDao
import com.ghostly.database.entities.PostWithAuthorsAndTags
import com.ghostly.mappers.toPost
import com.ghostly.mappers.toUpdatePostBody
import com.ghostly.network.ApiService
import com.ghostly.network.models.Result
import com.ghostly.posts.models.Post
import com.ghostly.posts.models.PostsResponse
import com.ghostly.posts.models.UpdatePostRequest
import com.ghostly.posts.models.UpdateRequestWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


interface PostRepository {
    suspend fun getOnePost(): Result<PostsResponse>

    fun getPosts(
        pageSize: Int,
        prefetchDistance: Int = pageSize,
    ): Flow<PagingData<PostWithAuthorsAndTags>>

    suspend fun publishPost(
        postId: String,
        requestWrapper: UpdateRequestWrapper,
    ): Result<Post>

    suspend fun updatePost(post: Post): Result<Post>

    suspend fun getPostById(id: String): Flow<Post?>

    suspend fun refreshPostFromServer(id: String): Result<Post>
}

@OptIn(ExperimentalPagingApi::class)
class PostRepositoryImpl(
    private val apiService: ApiService,
    private val postDao: PostDao,
    private val postRemoteMediator: PostRemoteMediator,
    private val postDataSource: PostDataSource,
) : PostRepository {
    override suspend fun getOnePost(): Result<PostsResponse> {
        return apiService.getPosts(1, 1)
    }

    override suspend fun publishPost(
        postId: String,
        requestWrapper: UpdateRequestWrapper,
    ): Result<Post> {
        return when (val result = apiService.publishPost(postId, requestWrapper)) {
            is Result.Success -> {
                val posts = result.data?.posts?.takeIf { it.isNotEmpty() } ?: return Result.Error(
                    -1,
                    "Something went wrong"
                )
                postDataSource.updatePost(posts.first())
                Result.Success(posts.first())
            }

            is Result.Error -> Result.Error(result.errorCode, result.message)
        }
    }

    override suspend fun updatePost(post: Post): Result<Post> {
        val request = UpdatePostRequest(
            posts = listOf(post.toUpdatePostBody())
        )

        return when (val result = apiService.updatePost(post.id, request)) {
            is Result.Success -> {
                val updatedPost = result.data?.posts?.firstOrNull()?.toPost(post)
                    ?: return Result.Error(-1, "No post data received")

                postDataSource.updatePost(updatedPost)
                Result.Success(updatedPost)
            }

            is Result.Error -> {
                println("PostRepository: Server update failed: ${result.message}")
                Result.Error(result.errorCode, result.message)
            }
        }
    }

    override fun getPosts(
        pageSize: Int,
        prefetchDistance: Int,
    ): Flow<PagingData<PostWithAuthorsAndTags>> {
        val pagingDataSource = { postDao.getAllPostsWithAuthorsAndTags() }

        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                initialLoadSize = pageSize,
                prefetchDistance = prefetchDistance
            ),
            pagingSourceFactory = pagingDataSource,
            remoteMediator = postRemoteMediator
        ).flow
    }

    override suspend fun getPostById(id: String): Flow<Post?> {
        return postDao.getPostWithAuthorsAndTags(id).map { postWithAuthorsAndTags ->
            postWithAuthorsAndTags?.toPost()
        }
    }

    override suspend fun refreshPostFromServer(id: String): Result<Post> {
        return when (val result = apiService.getPostById(id)) {
            is Result.Success -> {
                val postDto = result.data?.posts?.firstOrNull()
                    ?: return Result.Error(-1, "Could not fetch post data from server")

                val currentPost = postDao.getPostWithAuthorsAndTags(id).first()?.toPost()

                val updatedPost = postDto.toPost(currentPost)
                postDataSource.updatePost(updatedPost)
                Result.Success(updatedPost)
            }

            is Result.Error -> Result.Error(result.errorCode, result.message)
        }
    }
}