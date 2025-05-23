package com.chatwith.app.model

data class ChatMessage(
    val user: Boolean = false,
    val id: String = "",
    val sender: String = "",
    val text: String = "",
    val timestamp: Long = 0L
)

data class DeepSeekRequest(
    val model: String = "deepseek-chat",
    val messages: List<Message>,
    val temperature: Double = 0.7,
    val max_tokens: Int = 1000
)

data class Message(
    val role: String,  // "system", "user", ya da "assistant"
    val content: String
)

data class DeepSeekResponse(
    val id: String,
    val choices: List<Choice>,
    val created: Long,
    val model: String,
    val usage: Usage
)

data class Choice(
    val index: Int,
    val message: Message,
    val finish_reason: String
)

data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)