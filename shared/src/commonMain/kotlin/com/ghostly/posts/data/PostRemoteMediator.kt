// commonMain
package com.ghostly.posts.data

import app.cash.paging.ExperimentalPagingApi
import app.cash.paging.LoadType
import app.cash.paging.PagingState
import app.cash.paging.RemoteMediator
import com.ghostly.database.dao.PostDao
import com.ghostly.database.dao.RemoteKeysDao
import com.ghostly.database.entities.PostWithAuthorsAndTags
import com.ghostly.database.entities.RemoteKeys
import com.ghostly.network.ApiService
import com.ghostly.network.models.Result
import kotlinx.datetime.Clock
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@ExperimentalPagingApi
class PostRemoteMediator(
    private val apiService: ApiService,
    private val postDao: PostDao,
    private val remoteKeysDao: RemoteKeysDao,
    private val postDataSource: PostDataSource,
) : RemoteMediator<Int, PostWithAuthorsAndTags>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostWithAuthorsAndTags>,
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: 1
            }

            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
            }

            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
            }
        }

        return try {
            when (val postResult = apiService.getPosts(page, state.config.pageSize)) {
                is Result.Success -> {
                    if (loadType == LoadType.REFRESH) {
                        remoteKeysDao.clearRemoteKeys()
                        postDao.clearAll()
                    }

                    val posts = postResult.data?.posts ?: emptyList()

                    val endOfPaginationReached = posts.isEmpty()

                    val prevKey = if (page > 1) page - 1 else null
                    val nextKey = if (endOfPaginationReached) null else page + 1
                    val remoteKeys = posts.map {
                        RemoteKeys(
                            postId = it.id, prevKey = prevKey, currentPage = page, nextKey = nextKey
                        )
                    }

                    postDataSource.insertPosts(posts)
                    remoteKeysDao.insertAll(remoteKeys)
                    MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
                }

                is Result.Error -> {
                    MediatorResult.Error(RuntimeException("${postResult.errorCode}: ${postResult.message}"))
                }
            }

        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    override suspend fun initialize(): InitializeAction {
        val cacheTimeout = 1.toDuration(DurationUnit.HOURS).inWholeMilliseconds

        return if (Clock.System.now().toEpochMilliseconds() - (remoteKeysDao.getCreationTime()
                ?: 0) < cacheTimeout
        ) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, PostWithAuthorsAndTags>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.post?.id?.let { id ->
                remoteKeysDao.getRemoteKeyByPostId(id)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, PostWithAuthorsAndTags>): RemoteKeys? {
        return state.pages.firstOrNull {
            it.data.isNotEmpty()
        }?.data?.firstOrNull()?.let { postWrapper ->
            remoteKeysDao.getRemoteKeyByPostId(postWrapper.post.id)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, PostWithAuthorsAndTags>): RemoteKeys? {
        return state.pages.lastOrNull {
            it.data.isNotEmpty()
        }?.data?.lastOrNull()?.let { postWrapper ->
            remoteKeysDao.getRemoteKeyByPostId(postWrapper.post.id)
        }
    }
}
