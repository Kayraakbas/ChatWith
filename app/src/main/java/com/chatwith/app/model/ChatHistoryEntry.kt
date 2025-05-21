package com.chatwith.app.model

data class ChatHistoryEntry(
    // Firestore'da kayıtlı alan adlarıyla eşleşmeli
    val id: String = "",  // Boş varsayılan değer çok önemli
    val userId: String = "",
    val character: String = "",
    val timestamp: Long = 0L,
    val messages: List<ChatMessage> = listOf()
)