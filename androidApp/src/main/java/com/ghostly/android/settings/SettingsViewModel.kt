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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
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
    
    private val _allUsers = MutableStateFlow<List<User>>(emptyList())
    
    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage
    
    val users: StateFlow<Map<Role, List<User>>> = combine(
        _roles,
        _allUsers
    ) { roles, users ->
        println("Combining - Roles count: ${roles.size}, Users count: ${users.size}")
        
        // Filter to only show specific roles
        val allowedRoleNames = setOf("Administrator", "Editor", "Contributor", "Author")
        val filteredRoles = roles.filter { role ->
            allowedRoleNames.contains(role.name)
        }
        
        if (filteredRoles.isEmpty()) {
            emptyMap()
        } else {
            // Start with filtered roles having empty lists
            val roleUserMap = filteredRoles.associateWith { emptyList<User>() }.toMutableMap()
            
            // Then populate with actual users
            users.flatMap { user ->
                user.roles.map { role ->
                    role to user
                }
            }.groupBy(
                keySelector = { it.first },
                valueTransform = { it.second }
            ).forEach { (role, userList) ->
                if (allowedRoleNames.contains(role.name)) {
                    roleUserMap[role] = userList
                }
            }
            
            roleUserMap
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyMap()
    )

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
                        println("Error: No data in success result")
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
                        _allUsers.value = it.users
                    } ?: {
                        println("Error: No data in success result for users")
                        TODO("")
                    }
                }
            }
        }
    }

    suspend fun inviteStaff(invite: Invite): Boolean {
        return when (val result = settingsRepository.inviteUser(listOf(invite))) {
            is Result.Error -> {
                val errorMessage = when {
                    result.message?.contains("NoPermissionError") == true -> 
                        "You do not have permission to invite users"
                    result.message?.contains("Permission error") == true -> 
                        "Permission denied: Cannot invite users"
                    result.message?.contains("Error sending email") == true ->
                        "Error sending Email. Check your email configuration"
                    else -> 
                        "Failed to send invitation. Please try again."
                }
                _toastMessage.emit(errorMessage)
                false
            }

            is Result.Success -> {
                result.data?.let {
                    _toastMessage.emit("Invitation sent successfully!")
                    fetchUsers()
                    true
                } ?: run {
                    _toastMessage.emit("Failed to send invitation")
                    false
                }
            }
        }
    }
}