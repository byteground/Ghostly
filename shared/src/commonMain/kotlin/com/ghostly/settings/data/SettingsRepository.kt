package com.ghostly.settings.data

import com.ghostly.network.ApiService
import com.ghostly.network.Endpoint
import com.ghostly.network.models.Result
import com.ghostly.settings.models.Invite
import com.ghostly.settings.models.InviteRequestWrapper
import com.ghostly.settings.models.RolesResponse
import com.ghostly.settings.models.StaffData
import com.ghostly.settings.models.User
import com.ghostly.settings.models.UsersResponse
import io.ktor.client.call.body
import io.ktor.client.request.setBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

interface SettingsRepository {

    suspend fun getRoles(): Result<RolesResponse>

    suspend fun getUsers(): Result<UsersResponse>

    suspend fun getStaffData(): Result<StaffData>

    suspend fun inviteUser(invites: List<Invite>): Result<String>
}

private val ALLOWED_ROLES = setOf("Administrator", "Editor", "Contributor", "Author")

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

    override suspend fun getStaffData(): Result<StaffData> = withContext(Dispatchers.IO) {
        val rolesDeferred = async { getRoles() }
        val usersDeferred = async { getUsers() }

        val rolesResult = rolesDeferred.await()
        val usersResult = usersDeferred.await()

        if (rolesResult is Result.Error) {
            return@withContext rolesResult
        }
        if (usersResult is Result.Error) {
            return@withContext usersResult
        }

        val roles = (rolesResult as Result.Success).data?.roles ?: emptyList()
        val users = (usersResult as Result.Success).data?.users ?: emptyList()

        val filteredRoles = roles.filter { role ->
            ALLOWED_ROLES.contains(role.name)
        }

        val roleUserMap = filteredRoles.associateWith { emptyList<User>() }.toMutableMap()

        users.flatMap { user ->
            user.roles.map { role -> role to user }
        }.groupBy(
            keySelector = { it.first },
            valueTransform = { it.second }
        ).forEach { (role, userList) ->
            if (ALLOWED_ROLES.contains(role.name)) {
                roleUserMap[role] = userList
            }
        }

        Result.Success(StaffData(roleUserMap))
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