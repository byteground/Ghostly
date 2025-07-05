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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
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

    private val _users = MutableStateFlow<Map<Role, List<User>>>(emptyMap())
    val users: StateFlow<Map<Role, List<User>>> = _users.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage

    init {
        viewModelScope.launch {
            _storeName.value = dataStoreRepository.getString(DataStoreConstants.STORE_NAME)
            _storeIcon.value = dataStoreRepository.getString(DataStoreConstants.STORE_ICON)
        }

        fetchStaffData()
    }

    suspend fun clearLogin() {
        loginDetailsStore.delete()
        delay(1000)
    }

    private fun fetchStaffData() {
        viewModelScope.launch {
            when (val result = settingsRepository.getStaffData()) {
                is Result.Error -> {
                    _toastMessage.emit("Failed to load staff data: ${result.message}")
                }

                is Result.Success -> {
                    result.data?.let {
                        _users.value = it.roleUserMap
                    } ?: run {
                        _toastMessage.emit("No staff data available")
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
                    fetchStaffData()
                    true
                } ?: run {
                    _toastMessage.emit("Failed to send invitation")
                    false
                }
            }
        }
    }
}