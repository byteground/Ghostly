package com.ghostly.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import app.cash.paging.PagingSource
import com.ghostly.database.entities.AuthorEntity
import com.ghostly.database.entities.PostAuthorCrossRef
import com.ghostly.database.entities.PostEntity
import com.ghostly.database.entities.PostTagCrossRef
import com.ghostly.database.entities.PostWithAuthorsAndTags
import com.ghostly.database.entities.TagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostEntity>)

    @Update
    suspend fun updatePost(post: PostEntity)

    @Query("Delete From posts")
    suspend fun clearAll()

    @Transaction
    @Query("SELECT * FROM posts WHERE id = :postId")
    fun getPostWithAuthorsAndTags(postId: String): Flow<PostWithAuthorsAndTags?>

    @Transaction
    @Query("SELECT * FROM posts")
    fun getAllPostsWithAuthorsAndTags(): PagingSource<Int, PostWithAuthorsAndTags>
}

@Dao
interface AuthorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuthor(author: AuthorEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuthors(authors: List<AuthorEntity>)

    @Query("DELETE FROM authors")
    suspend fun clearAll()
}

@Dao
interface TagDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: TagEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTags(tags: List<TagEntity>)

    @Query("DELETE FROM tags")
    suspend fun clearAll()
}

@Dao
interface PostAuthorCrossRefDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPostAuthorCrossRef(crossRef: PostAuthorCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPostAuthorCrossRef(crossRefs: List<PostAuthorCrossRef>)

    @Query("DELETE FROM post_author_cross_ref")
    suspend fun clearAll()
}

@Dao
interface PostTagCrossRefDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPostTagCrossRef(crossRef: PostTagCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPostTagCrossRef(crossRefs: List<PostTagCrossRef>)

    @Query("DELETE FROM post_tag_cross_ref WHERE postId = :postId")
    suspend fun clearPostTagCrossRefs(postId: String)

    @Query("DELETE FROM post_tag_cross_ref")
    suspend fun clearAll()
}