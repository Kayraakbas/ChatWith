package com.chatwith.app.services
import com.chatwith.app.model.ChatHistoryEntry
import com.chatwith.app.model.ChatMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext




class ChatService {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    suspend fun saveChatHistory(character: String, messages: List<ChatMessage>) {
        val userId = auth.currentUser?.uid ?: return
        val chatId = "$userId-${character.lowercase().replace(" ", "_")}"

        val chatHistory = ChatHistoryEntry(
            id = chatId,
            userId = userId,
            character = character,
            timestamp = System.currentTimeMillis(),
            messages = messages
        )

        withContext(Dispatchers.IO) {
            db.collection("chats").document(chatId).set(chatHistory)
        }
    }

    suspend fun loadChatHistory(character: String): List<ChatMessage> {
        val userId = auth.currentUser?.uid ?: return emptyList()
        val chatId = "$userId-${character.lowercase().replace(" ", "_")}"

        return withContext(Dispatchers.IO) {
            try {
                val doc = db.collection("chats").document(chatId).get().await()
                val history = doc.toObject(ChatHistoryEntry::class.java)
                history?.messages ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    suspend fun clearChatHistory(character: String) {
        val userId = auth.currentUser?.uid ?: return
        val chatId = "$userId-${character.lowercase().replace(" ", "_")}"

        withContext(Dispatchers.IO) {
            db.collection("chats").document(chatId).delete()
        }
    }

    suspend fun getAllChatHistories(): List<ChatHistoryEntry> {
        val userId = auth.currentUser?.uid

        if (userId == null) {
            println("Kullanıcı oturumu açık değil")
            return emptyList()
        }

        println("Kullanıcı ID'si ile sorgu başlatılıyor: $userId")

        return withContext(Dispatchers.IO) {
            try {
                // Sunucu tarafında filtreleme yaparak sadece ilgili belgeleri çekelim
                val querySnapshot = db.collection("chats")
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()

                println("Sorgu tamamlandı. Bulunan belge sayısı: ${querySnapshot.size()}")

                val result = querySnapshot.documents.mapNotNull { doc ->
                    try {
                        doc.toObject(ChatHistoryEntry::class.java)
                    } catch (e: Exception) {
                        println("Belge dönüştürme hatası (ID: ${doc.id}): ${e.message}")
                        null
                    }
                }

                println("İşlenen toplam belge sayısı: ${result.size}")
                result

            } catch (e: Exception) {
                println("Sohbet geçmişi çekilirken hata: ${e.message}")
                e.printStackTrace()
                emptyList()
            }
        }
    }

//    suspend fun getAllChatHistories(): List<ChatHistoryEntry> {
//        val userId = auth.currentUser?.uid ?: return emptyList()
//
//        return withContext(Dispatchers.IO) {
//            try {
//                val querySnapshot = db.collection("chats")
//                    .whereEqualTo("userId", userId)
//                    .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
//                    .get()
//                    .await()
//
//                querySnapshot.documents.mapNotNull { doc ->
//                    doc.toObject(ChatHistoryEntry::class.java)
//                }
//            } catch (e: Exception) {
//                emptyList()
//            }
//        }
//    }
}