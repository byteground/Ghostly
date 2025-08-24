package com.ghostly.posts.data

import com.ghostly.database.dao.AuthorDao
import com.ghostly.database.dao.PostAuthorCrossRefDao
import com.ghostly.database.dao.PostDao
import com.ghostly.database.dao.PostTagCrossRefDao
import com.ghostly.database.dao.TagDao
import com.ghostly.database.entities.AuthorEntity
import com.ghostly.database.entities.PostAuthorCrossRef
import com.ghostly.database.entities.PostTagCrossRef
import com.ghostly.database.entities.TagEntity
import com.ghostly.mappers.toPostEntity
import com.ghostly.posts.models.Post

interface PostDataSource {
    suspend fun insertPosts(posts: List<Post>)
    suspend fun updatePost(post: Post)
    suspend fun refreshPosts(posts: List<Post>)
}

class LocalPostDataSource(
    private val postDao: PostDao,
    private val authorDao: AuthorDao,
    private val tagDao: TagDao,
    private val postAuthorCrossRefDao: PostAuthorCrossRefDao,
    private val postTagCrossRefDao: PostTagCrossRefDao,
) : PostDataSource {
    override suspend fun insertPosts(posts: List<Post>) {
        posts.map { it.toPostEntity() }.let {
            postDao.insertPosts(it)
        }
        
        authorDao.insertAuthors(posts.flatMap {
            it.authors.map { author ->
                AuthorEntity(
                    author.id, author.name, author.slug, author.profileImage
                )
            }
        })
        
        tagDao.insertTags(posts.flatMap {
            it.tags.mapNotNull { tag ->
                tag.id?.let { id ->
                    TagEntity(
                        id, tag.name, tag.slug
                    )
                }
            }
        })
        
        postAuthorCrossRefDao.insertPostAuthorCrossRef(posts.flatMap { post ->
            post.authors.map { author ->
                PostAuthorCrossRef(
                    post.id, author.id
                )
            }
        })
        
        postTagCrossRefDao.insertPostTagCrossRef(posts.flatMap { post ->
            post.tags.mapNotNull { tag ->
                tag.id?.let { id ->
                    PostTagCrossRef(
                        post.id, id
                    )
                }
            }
        })
    }

    override suspend fun updatePost(post: Post) {
        postDao.updatePost(post.toPostEntity())
        
        authorDao.insertAuthors(
            post.authors.map { author ->
                AuthorEntity(
                    author.id, author.name, author.slug, author.profileImage
                )
            }
        )
        
        tagDao.insertTags(post.tags.mapNotNull { tag ->
            tag.id?.let { id ->
                TagEntity(
                    id, tag.name, tag.slug
                )
            }
        })
        
        postTagCrossRefDao.clearPostTagCrossRefs(post.id)
        postTagCrossRefDao.insertPostTagCrossRef(
            post.tags.mapNotNull { tag ->
                tag.id?.let { id ->
                    PostTagCrossRef(
                        post.id, id
                    )
                }
            }
        )
        
        postAuthorCrossRefDao.insertPostAuthorCrossRef(
            post.authors.map { author ->
                PostAuthorCrossRef(
                    post.id, author.id
                )
            }
        )
    }
    
    override suspend fun refreshPosts(posts: List<Post>) {
        posts.map { it.toPostEntity() }.let {
            postDao.insertPosts(it)
        }
        
        authorDao.insertAuthors(posts.flatMap {
            it.authors.map { author ->
                AuthorEntity(
                    author.id, author.name, author.slug, author.profileImage
                )
            }
        })
        
        tagDao.insertTags(posts.flatMap {
            it.tags.mapNotNull { tag ->
                tag.id?.let { id ->
                    TagEntity(
                        id, tag.name, tag.slug
                    )
                }
            }
        })
        
        postAuthorCrossRefDao.insertPostAuthorCrossRef(posts.flatMap { post ->
            post.authors.map { author ->
                PostAuthorCrossRef(
                    post.id, author.id
                )
            }
        })
        
        posts.forEach { post ->
            postTagCrossRefDao.clearPostTagCrossRefs(post.id)
            postTagCrossRefDao.insertPostTagCrossRef(
                post.tags.mapNotNull { tag ->
                    tag.id?.let { id ->
                        PostTagCrossRef(
                            post.id, id
                        )
                    }
                }
            )
        }
    }
}