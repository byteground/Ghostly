package com.ghostly.settings.data

import com.ghostly.network.ApiService
import com.ghostly.network.Endpoint
import com.ghostly.network.models.Result
import com.ghostly.settings.models.Invite
import com.ghostly.settings.models.InviteRequestWrapper
import com.ghostly.settings.models.RolesResponse
import com.ghostly.settings.models.UsersResponse
import io.ktor.client.call.body
import io.ktor.client.request.setBody

interface SettingsRepository {

    suspend fun getRoles(): Result<RolesResponse>

    suspend fun getUsers(): Result<UsersResponse>

    suspend fun inviteUser(invites: List<Invite>): Result<String>
}

internal class SettingsRepositoryImpl(
    private val apiService: ApiService,
) : SettingsRepository {
    override suspend fun getRoles(): Result<RolesResponse> {
        return apiService.get(
            endpoint = Endpoint.GET_ROLES,
            getBody = { it.body<RolesResponse>() }
        )
    }

    override suspend fun getUsers(): Result<UsersResponse> {
        return apiService.get(
            endpoint = Endpoint.GET_USERS,
            getBody = { it.body<UsersResponse>() }
        )
    }

    override suspend fun inviteUser(invites: List<Invite>): Result<String> {
        return apiService.post(
            endpoint = Endpoint.INVITE_USER,
            getBody = { it.body<String>() }
        ) {
            setBody(InviteRequestWrapper(invites))
        }
    }

}