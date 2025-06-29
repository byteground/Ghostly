package com.ghostly

import androidx.paging.ExperimentalPagingApi
import com.ghostly.database.daoModule
import com.ghostly.posts.data.GetPostsUseCase
import com.ghostly.posts.data.GetPostsUseCaseImpl
import com.ghostly.posts.data.LocalPostDataSource
import com.ghostly.posts.data.PostDataSource
import com.ghostly.posts.data.PostRemoteMediator
import com.ghostly.posts.data.PostRepository
import com.ghostly.posts.data.PostRepositoryImpl
import com.ghostly.posts.data.EditPostUseCase
import com.ghostly.posts.data.EditPostUseCaseImpl
import com.ghostly.settings.data.SettingsRepository
import com.ghostly.settings.data.SettingsRepositoryImpl
import org.koin.dsl.module

@OptIn(ExperimentalPagingApi::class)
private val ghostCommonModule = module {
    single<PostDataSource> { LocalPostDataSource(get(), get(), get(), get(), get()) }
    single<PostRepository> { PostRepositoryImpl(get(), get(), get(), get()) }
    single<EditPostUseCase> { EditPostUseCaseImpl(get()) }
    single<GetPostsUseCase> { GetPostsUseCaseImpl(get()) }
    single { PostRemoteMediator(get(), get(), get(), get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
}

val ghostCommonModules = listOf(daoModule, ghostCommonModule)