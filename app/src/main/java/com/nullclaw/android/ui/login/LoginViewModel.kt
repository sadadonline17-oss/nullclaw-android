package com.nullclaw.android.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nullclaw.android.data.repository.ChatRepository
import com.nullclaw.android.util.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Login screen.
 * Handles pairing with NullClaw gateway.
 */
class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ChatRepository(SessionManager(application))

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        // Initialize session with stored values
        repository.initializeSession()
        
        // Check if already authenticated
        if (repository.isAuthenticated()) {
            _uiState.update { it.copy(isAuthenticated = true) }
        }
    }

    /**
     * Update the server URL.
     */
    fun updateServerUrl(url: String) {
        _uiState.update { it.copy(serverUrl = url) }
        repository.updateServerUrl(url)
    }

    /**
     * Update the pairing code input.
     */
    fun updatePairingCode(code: String) {
        _uiState.update { it.copy(pairingCode = code) }
    }

    /**
     * Check server health.
     */
    fun checkHealth() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            repository.checkHealth()
                .onSuccess { response ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            serverHealthy = response.isHealthy,
                            error = if (!response.isHealthy) "Server not healthy" else null
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            serverHealthy = false,
                            error = "Cannot connect to server: ${error.message}"
                        )
                    }
                }
        }
    }

    /**
     * Attempt to pair with the NullClaw gateway.
     */
    fun pair() {
        val code = _uiState.value.pairingCode.trim()
        if (code.length != 6) {
            _uiState.update { it.copy(error = "Pairing code must be 6 digits") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            repository.pair(code)
                .onSuccess {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            isAuthenticated = true,
                            error = null
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Pairing failed"
                        )
                    }
                }
        }
    }

    /**
     * Clear any error message.
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

/**
 * UI state for the Login screen.
 */
data class LoginUiState(
    val serverUrl: String = SessionManager.DEFAULT_SERVER_URL,
    val pairingCode: String = "",
    val isLoading: Boolean = false,
    val serverHealthy: Boolean? = null,
    val isAuthenticated: Boolean = false,
    val error: String? = null
)