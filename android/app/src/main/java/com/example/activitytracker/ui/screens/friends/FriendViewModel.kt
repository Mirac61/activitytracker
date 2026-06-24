package com.example.activitytracker.ui.screens.friends

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.activitytracker.data.remote.dto.FriendDto
import com.example.activitytracker.data.remote.dto.FriendRequestDto
import com.example.activitytracker.data.repository.FriendRepository
import kotlinx.coroutines.launch
import java.util.UUID

class FriendViewModel(
    private val repository: FriendRepository,
    private val userId: UUID
) : ViewModel() {


    private val _friends = MutableLiveData<List<FriendDto>>(emptyList())
    val friends: LiveData<List<FriendDto>> = _friends

    private val _pendingRequests = MutableLiveData<List<FriendRequestDto>>(emptyList())
    val pendingRequests: LiveData<List<FriendRequestDto>> = _pendingRequests

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

    private val _successMessage = MutableLiveData<String?>(null)
    val successMessage: LiveData<String?> = _successMessage

    private val _ownFriendCode = MutableLiveData<String>("")
    val ownFriendCode: LiveData<String> = _ownFriendCode


    init {
        loadFriends()
        loadPendingRequests()
        loadOwnFriendCode()
    }


    fun loadFriends() {
        viewModelScope.launch {
            val result = repository.getFriends(userId)
            if (result.isSuccess) {
                _friends.value = result.getOrNull() ?: emptyList()
            } else {
                _errorMessage.value = "Freunde konnten nicht geladen werden"
            }
        }
    }

    private fun loadOwnFriendCode() {
        viewModelScope.launch {
            val result = repository.getFriendCode(userId)
            if (result.isSuccess) {
                val code = result.getOrNull() ?: ""
                android.util.Log.d("FriendViewModel", "FriendCode geladen: $code")
                _ownFriendCode.value = code
            } else {
                android.util.Log.e("FriendViewModel", "Fehler: ${result.exceptionOrNull()?.message}")
            }
        }
    }


    fun loadPendingRequests() {
        viewModelScope.launch {
            val result = repository.getPendingRequests(userId)
            if (result.isSuccess) {
                val requests = result.getOrNull() ?: emptyList()
                android.util.Log.d("FriendViewModel", "Anfragen geladen: ${requests.size}")
                _pendingRequests.value = requests
            } else {
                android.util.Log.e("FriendViewModel", "Fehler Anfragen: ${result.exceptionOrNull()?.message}")
            }
        }
    }


    fun sendFriendRequest(friendCode: String) {
        viewModelScope.launch {
            val result = repository.sendFriendRequest(userId, friendCode)
            if (result.isSuccess) {
                _successMessage.value = "Anfrage gesendet"
            } else {
                _errorMessage.value = "Anfrage konnte nicht gesendet werden"
            }
        }
    }


    fun acceptRequest(requestId: UUID) {
        viewModelScope.launch {
            val result = repository.acceptRequest(requestId)
            if (result.isSuccess) {
                _successMessage.value = "Freund hinzugefügt"
                loadFriends()
                loadPendingRequests()
            } else {
                _errorMessage.value = "Anfrage konnte nicht angenommen werden"
            }
        }
    }


    fun declineRequest(requestId: UUID) {
        viewModelScope.launch {
            val result = repository.declineRequest(requestId)
            if (result.isSuccess) {
                loadPendingRequests()
            } else {
                _errorMessage.value = "Anfrage konnte nicht abgelehnt werden"
            }
        }
    }


    fun removeFriend(friendId: UUID) {
        viewModelScope.launch {
            val result = repository.removeFriend(userId, friendId)
            if (result.isSuccess) {
                _successMessage.value = "Freund entfernt"
                loadFriends()
            } else {
                _errorMessage.value = "Freund konnte nicht entfernt werden"
            }
        }
    }


    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }
}


class FriendViewModelFactory(
    private val repository: FriendRepository,
    private val userId: UUID
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FriendViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FriendViewModel(repository, userId) as T
        }
        throw IllegalArgumentException("Unknown Class for View Model")
    }
}