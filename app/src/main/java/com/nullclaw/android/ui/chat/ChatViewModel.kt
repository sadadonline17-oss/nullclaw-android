package com.nullclaw.android.ui.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nullclaw.android.data.model.ChatMessage
import com.nullclaw.android.data.repository.ChatRepository
import com.nullclaw.android.util.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Chat screen.
 * Manages chat messages and communication with NullClaw backend.
 */
class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ChatRepository(SessionManager(application))

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        repository.initializeSession()
    }

    /**
     * Send a message to the NullClaw agent.
     */
    fun sendMessage(content: String) {
        if (content.isBlank()) return

        val userMessage = ChatMessage.userMessage(content)
        
        viewModelScope.launch {
            // Add user message immediately
            _uiState.update { currentState ->
                currentState.copy(
                    messages = currentState.messages + userMessage,
                    inputText = "",
                    isLoading = true,
                    error = null
                )
            }

            // Send to backend
            repository.sendMessage(content)
                .onSuccess { response ->
                    val assistantMessage = ChatMessage.assistantMessage(
                        response.response ?: "No response"
                    )
                    _uiState.update { currentState ->
                        currentState.copy(
                            messages = currentState.messages + assistantMessage,
                            isLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to send message"
                        )
                    }
                }
        }
    }

    /**
     * Update the input text field.
     */
    fun updateInputText(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    /**
     * Clear any error message.
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    /**
     * Logout and clear session.
     */
    fun logout() {
        repository.logout()
        _uiState.update { ChatUiState() }
    }
}

/**
 * UI state for the Chat screen.
 */
data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)