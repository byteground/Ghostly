package com.ghostly.android.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ghostly.datastore.DataStoreConstants
import com.ghostly.datastore.DataStoreRepository
import com.ghostly.datastore.LoginDetailsStore
import com.ghostly.network.models.Result
import com.ghostly.settings.data.SettingsRepository
import com.ghostly.settings.models.Invite
import com.ghostly.settings.models.Role
import com.ghostly.settings.models.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val dataStoreRepository: DataStoreRepository,
    private val loginDetailsStore: LoginDetailsStore,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _storeName = MutableStateFlow<String?>(null)
    val storeName: StateFlow<String?> = _storeName

    private val _storeIcon = MutableStateFlow<String?>(null)
    val storeIcon: StateFlow<String?> = _storeIcon

    private val _roles = MutableStateFlow<List<Role>>(emptyList())
    val roles = _roles.asStateFlow()

    private val _users = MutableStateFlow<Map<Role, List<User>>>(emptyMap())
    val users = _users.asStateFlow()

    init {
        viewModelScope.launch {
            _storeName.value = dataStoreRepository.getString(DataStoreConstants.STORE_NAME)
            _storeIcon.value = dataStoreRepository.getString(DataStoreConstants.STORE_ICON)
        }
    }

    suspend fun clearLogin() {
        loginDetailsStore.delete()
        delay(1000)
    }

    fun fetchRoles() {
        viewModelScope.launch {
            when (val result = settingsRepository.getRoles()) {
                is Result.Error -> {
                    println("Error: $result")
                    TODO()
                }

                is Result.Success -> {
                    result.data?.let {
                        _roles.value = it.roles
                    } ?: {
                        println("Error: $result")
                        TODO("")
                    }
                }
            }
        }
    }

    fun fetchUsers() {
        viewModelScope.launch {
            when (val result = settingsRepository.getUsers()) {
                is Result.Error -> {
                    println("Error: $result")
                    TODO()
                }

                is Result.Success -> {
                    result.data?.let {
                        val roleUserMap = it.users.flatMap { user ->
                            user.roles.map { role ->
                                role to user
                            }
                        }.groupBy(
                            keySelector = { it.first },
                            valueTransform = { it.second }
                        )
                        println("Goku: Just user ${it.users}")
                        println("Goku: User and Roles ${roleUserMap}")
                        _users.value = roleUserMap
                    } ?: {
                        println("Error: $result")
                        TODO("")
                    }
                }
            }
        }
    }

    suspend fun inviteStaff(invite: Invite) {
        when (val result = settingsRepository.inviteUser(listOf(invite))) {
            is Result.Error -> {
                println("Error: $result")
                TODO()
            }

            is Result.Success -> {
                result.data?.let {
                    println("Success: ${result.data}")
                    TODO("")
                } ?: {
                    println("Error: $result")
                    TODO("")
                }
            }
        }
    }
}