package com.chatwith.app.ui

import com.chatwith.app.model.Message
import com.chatwith.app.model.DeepSeekRequest
import com.chatwith.app.services.DeepSeekClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chatwith.app.model.ChatMessage
import com.chatwith.app.services.ChatService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.animation.core.*
import androidx.compose.material.icons.filled.Delete
import kotlinx.coroutines.withTimeout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    selectedCharacter: String,
    onBack: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val messages = remember { mutableStateListOf<ChatMessage>() }
    var input by remember { mutableStateOf("") }
    var isTyping by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val colors = MaterialTheme.colorScheme
    val chatService = remember { ChatService() }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    LaunchedEffect(selectedCharacter) {
        val history = chatService.loadChatHistory(selectedCharacter)
        messages.clear()
        messages.addAll(history)

    }
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Delete Chat") },
            text = { Text("Are you sure you want to delete this chat?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmDialog = false
                        scope.launch {
                            chatService.clearChatHistory(selectedCharacter)
                            messages.clear()
                            onBack()
                        }
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Talking with: $selectedCharacter", fontSize = 18.sp, color = colors.onSurface)
                },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = colors.onSurface)
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteConfirmDialog = true }) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Delete,
                            contentDescription = "Delete Chat",
                            tint = colors.onSurface
                        )
                    }
                    IconButton(onClick = { onLogout() }) {
                        Icon(Icons.Filled.ExitToApp, contentDescription = "Logout", tint = colors.onSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.surface,
                    navigationIconContentColor = colors.onSurface
                )
            )
        },
        containerColor = colors.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                reverseLayout = true
            ) {
                if (isTyping) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = colors.surface,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(12.dp)
                            ) {
                                Row {
                                    Text(text = selectedCharacter + " typing", color = colors.onSurface)
                                    LoadingDots()
                                }
                            }
                        }
                    }
                }
                items(messages.reversed()) { message ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (message.isUser) colors.primary else colors.surface,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(12.dp)
                                .widthIn(max = 280.dp)
                        ) {
                            Text(
                                message.text,
                                color = if (message.isUser) colors.onPrimary else colors.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.surface, RoundedCornerShape(24.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    if (input.isBlank()) {
                        Text(
                            text = "Enter message...",
                            color = colors.onSurface.copy(alpha = 0.4f),
                            fontSize = 16.sp
                        )
                    }
                    BasicTextField(
                        value = input,
                        onValueChange = { input = it },
                        textStyle = TextStyle(color = colors.onSurface, fontSize = 16.sp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                IconButton(
                    onClick = {
                            scope.launch {
                                if (input.isNotBlank()) {
                                    messages.add(ChatMessage(isUser = true, text = input, timestamp = System.currentTimeMillis()))
                                    val userInput = input
                                    input = ""

                                    isTyping = true

                                    val aiReply = generateAIResponse(userInput, selectedCharacter)


                                    isTyping = false
                                    messages.add(ChatMessage(isUser = false, text = aiReply, timestamp = System.currentTimeMillis()))
                                    chatService.saveChatHistory(selectedCharacter, messages.toList())                                }
                            }
                    }
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = colors.primary)
                }
            }
        }
    }
}

suspend fun generateAIResponse(userInput: String, character: String): String {
    return try {
        val messages = listOf(
            Message("system", "You will role play as $character. Interact with user as you are an that exact character like an role play."),
            Message("user", userInput)
        )

        val request = DeepSeekRequest(
            messages = messages,
            temperature = 0.7
        )

        val response = withTimeout(35000) {
            DeepSeekClient.apiService.generateResponse(
                authHeader = DeepSeekClient.getAuthHeader(),
                request = request
            )
        }

        response.choices.firstOrNull()?.message?.content
            ?: "Üzgünüm, yanıt oluşturulurken bir hata meydana geldi."
    } catch (e: Exception) {
        "API isteği sırasında bir hata oluştu: ${e.localizedMessage}"
    }
}

@Composable
fun LoadingDots() {
    val dotsCount by rememberInfiniteTransition().animateValue(
        initialValue = 0,
        targetValue = 3,
        typeConverter = Int.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Text("." + ".".repeat(dotsCount % 4))
}