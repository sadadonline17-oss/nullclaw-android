package com.nullclaw.android.data.model

/**
 * Represents a chat message in the conversation.
 */
data class ChatMessage(
    val id: String,
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        fun userMessage(content: String): ChatMessage {
            return ChatMessage(
                id = java.util.UUID.randomUUID().toString(),
                content = content,
                isFromUser = true
            )
        }

        fun assistantMessage(content: String): ChatMessage {
            return ChatMessage(
                id = java.util.UUID.randomUUID().toString(),
                content = content,
                isFromUser = false
            )
        }
    }
}