package com.ghostly.mappers

import com.ghostly.database.entities.AuthorEntity
import com.ghostly.database.entities.PostEntity
import com.ghostly.database.entities.PostWithAuthorsAndTags
import com.ghostly.database.entities.TagEntity
import com.ghostly.posts.models.Author
import com.ghostly.posts.models.AuthorDto
import com.ghostly.posts.models.Post
import com.ghostly.posts.models.PostDto
import com.ghostly.posts.models.Tag
import com.ghostly.posts.models.TagDto
import com.ghostly.posts.models.UpdatePostBody
import kotlinx.datetime.Clock

internal fun PostWithAuthorsAndTags.toPost(): Post {
    return Post(
        id = post.id,
        slug = post.slug,
        title = post.title,
        content = post.html,
        featureImage = post.featureImage,
        status = post.status,
        createdAt = post.createdAt,
        updatedAt = post.updatedAt,
        publishedAt = post.publishedAt,
        url = post.url,
        visibility = post.visibility,
        excerpt = post.excerpt,
        authors = authors.map { it.toAuthor() },
        tags = tags.map { it.toTag() }
    )
}

internal fun Post.toPostEntity(): PostEntity {
    return PostEntity(
        id,
        slug,
        title,
        content,
        featureImage,
        status,
        visibility,
        createdAt,
        updatedAt,
        publishedAt,
        url,
        excerpt
    )
}

internal fun TagEntity.toTag(): Tag {
    return Tag(
        id = id,
        name = name,
        slug = slug
    )
}

internal fun AuthorEntity.toAuthor(): Author {
    return Author(
        id = id,
        name = name,
        profileImage = profileImage,
        slug = slug
    )
}

internal fun AuthorDto.toAuthor(): Author {
    return Author(
        id = id,
        name = name,
        profileImage = profileImage,
        slug = slug
    )
}

internal fun TagDto.toTag(): Tag {
    return Tag(
        id = id ?: "",
        name = name,
        slug = slug ?: ""
    )
}

fun PostDto.toPost(originalPost: Post? = null): Post {
    return Post(
        id = this.id,
        slug = originalPost?.slug ?: "",
        createdAt = originalPost?.createdAt ?: "",
        title = this.title,
        content = this.content ?: originalPost?.content ?: "",
        featureImage = this.featureImage,
        status = this.status,
        publishedAt = this.publishedAt,
        updatedAt = this.updatedAt,
        url = this.url,
        visibility = this.visibility,
        excerpt = this.excerpt,
        authors = this.authors?.map { authorDto ->
            Author(
                id = authorDto.id,
                name = authorDto.name,
                profileImage = authorDto.profileImage,
                slug = authorDto.slug
            )
        } ?: originalPost?.authors ?: emptyList(),
        tags = this.tags?.map { tagDto ->
            Tag(
                id = tagDto.id ?: "temp_${Clock.System.now().epochSeconds}",
                name = tagDto.name,
                slug = tagDto.slug ?: tagDto.name.lowercase().replace(" ", "-")
            )
        } ?: originalPost?.tags ?: emptyList()
    )
}

fun Post.toUpdatePostBody() = UpdatePostBody(
    id = this.id,
    title = this.title,
    content = this.content,
    excerpt = this.excerpt,
    tags = this.tags.map { tag ->
        TagDto(
            id = tag.id,
            name = tag.name,
            slug = tag.slug
        )
    },
    status = this.status,
    authorId = this.authors.firstOrNull()?.id,
    featureImage = this.featureImage,
    updatedAt = this.updatedAt,
    visibility = this.visibility,
    publishedAt = this.publishedAt,
    url = this.url,
    slug = this.slug
)