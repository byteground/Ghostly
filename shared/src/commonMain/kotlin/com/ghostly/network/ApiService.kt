package com.ghostly.network

import com.ghostly.datastore.LoginDetailsStore
import com.ghostly.login.models.LoginDetails
import com.ghostly.login.models.SiteResponse
import com.ghostly.network.models.Result
import com.ghostly.network.models.Token
import com.ghostly.posts.models.PostsResponse
import com.ghostly.posts.models.UpdateRequestWrapper
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

interface ApiService {
    suspend fun getPosts(page: Int, pageSize: Int): Result<PostsResponse>
    suspend fun getSiteDetails(url: String): Result<SiteResponse>
    suspend fun publishPost(postId: String, request: UpdateRequestWrapper): Result<PostsResponse>

    suspend fun <T> get(
        endpoint: Endpoint,
        getBody: suspend (HttpResponse) -> T,
        block: HttpRequestBuilder.() -> Unit = {},
    ): Result<T>

    suspend fun <T> post(
        endpoint: Endpoint,
        getBody: suspend (HttpResponse) -> T,
        block: HttpRequestBuilder.() -> Unit = {},
    ): Result<T>
}

class ApiServiceImpl(
    private val client: HttpClient,
    private val tokenProvider: TokenProvider,
    private val loginDetailsStore: LoginDetailsStore,
) : ApiService {

    private suspend fun getLoginDetails(): LoginDetails? {
        return loginDetailsStore.get()
    }

    private suspend fun tryAndGetToken(): Token? {
        return tokenProvider.generateToken()
    }

    override suspend fun <T> get(
        endpoint: Endpoint,
        getBody: suspend (HttpResponse) -> T,
        block: HttpRequestBuilder.() -> Unit,
    ): Result<T> = withContext(Dispatchers.IO) {
        val loginDetails =
            getLoginDetails() ?: return@withContext Result.Error(-1, "Invalid Login Details")

        val token =
            tryAndGetToken() ?: return@withContext Result.Error(-1, "Unable to generate token")

        val response: HttpResponse = client.get("${loginDetails.domainUrl}${endpoint.path}") {
            block.invoke(this)
            header("Authorization", "Ghost ${token.token}")
        }

        when {
            response.status == HttpStatusCode.Unauthorized -> {
                return@withContext Result.Error(
                    HttpStatusCode.Unauthorized.value,
                    "Invalid API Key"
                )
            }

            response.status != HttpStatusCode.OK -> {
                return@withContext Result.Error(response.status.value, response.bodyAsText())
            }

            else -> {
                Result.Success(getBody(response))
            }
        }
    }

    override suspend fun <T> post(
        endpoint: Endpoint,
        getBody: suspend (HttpResponse) -> T,
        block: HttpRequestBuilder.() -> Unit
    ): Result<T> = withContext(Dispatchers.IO) {
        val loginDetails =
            getLoginDetails() ?: return@withContext Result.Error(-1, "Invalid Login Details")

        val token =
            tryAndGetToken() ?: return@withContext Result.Error(-1, "Unable to generate token")

        val response: HttpResponse = client.post("${loginDetails.domainUrl}${endpoint.path}") {
            block.invoke(this)
            header("Authorization", "Ghost ${token.token}")
        }

        when {
            response.status == HttpStatusCode.Unauthorized -> {
                return@withContext Result.Error(
                    HttpStatusCode.Unauthorized.value,
                    "Invalid API Key"
                )
            }

            response.status != HttpStatusCode.OK -> {
                return@withContext Result.Error(response.status.value, response.bodyAsText())
            }

            else -> {
                Result.Success(getBody(response))
            }
        }
    }

    override suspend fun getPosts(page: Int, pageSize: Int) = withContext(Dispatchers.IO) {
        val loginDetails =
            getLoginDetails() ?: return@withContext Result.Error(-1, "Invalid Login Details")

        val token =
            tryAndGetToken() ?: return@withContext Result.Error(-1, "Unable to generate token")

        val response: HttpResponse =
            client.get("${loginDetails.domainUrl}/api/admin/posts/?formats=html") {
                parameter("page", page)
                parameter("limit", pageSize)
                header("Authorization", "Ghost ${token.token}")
            }

        when {
            response.status == HttpStatusCode.Unauthorized -> {
                return@withContext Result.Error(
                    HttpStatusCode.Unauthorized.value,
                    "Invalid API Key"
                )
            }

            response.status != HttpStatusCode.OK -> {
                return@withContext Result.Error(response.status.value, response.bodyAsText())
            }

            else -> {
                Result.Success(response.body<PostsResponse>())
            }
        }
    }

    override suspend fun getSiteDetails(url: String): Result<SiteResponse> =
        withContext(Dispatchers.IO) {
            val response: HttpResponse = client.get("${url}/api/admin/site/")

            if (response.status == HttpStatusCode.OK) {
                return@withContext Result.Success(response.body<SiteResponse>())
            }
            Result.Error(
                errorCode = response.status.value,
                message = "Invalid Site Details"
            )
        }

    override suspend fun publishPost(
        postId: String,
        request: UpdateRequestWrapper,
    ): Result<PostsResponse> =
        withContext(Dispatchers.IO) {
            val loginDetails =
                getLoginDetails() ?: return@withContext Result.Error(-1, "Invalid Login Details")

            val token =
                tryAndGetToken() ?: return@withContext Result.Error(-1, "Unable to generate token")
            val response: HttpResponse =
                client.put("${loginDetails.domainUrl}/api/admin/posts?formats=html") {
                    url {
                        appendPathSegments(postId)
                    }
                    header("Authorization", "Ghost ${token.token}")
                    contentType(ContentType.Application.Json)
                    setBody(request)
                }

            when {
                response.status == HttpStatusCode.Unauthorized -> {
                    return@withContext Result.Error(
                        HttpStatusCode.Unauthorized.value,
                        "Invalid API Key"
                    )
                }

                response.status != HttpStatusCode.OK -> {
                    return@withContext Result.Error(response.status.value, response.bodyAsText())
                }

                else -> {
                    Result.Success(response.body<PostsResponse>())
                }
            }
        }
}

fun logUnlimited(tag: String, string: String) {
    val maxLogSize = 1000
    string.chunked(maxLogSize).forEach { println("$tag: $it ") }
}