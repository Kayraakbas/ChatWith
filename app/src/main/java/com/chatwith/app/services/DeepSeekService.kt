package com.chatwith.app.services

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.chatwith.app.model.DeepSeekRequest
import com.chatwith.app.model.DeepSeekResponse
import java.util.concurrent.TimeUnit

interface DeepSeekApiService {
    @POST("v1/chat/completions")
    suspend fun generateResponse(
        @Header("Authorization") authHeader: String,
        @Body request: DeepSeekRequest
    ): DeepSeekResponse
}


object DeepSeekClient {
    private const val BASE_URL = "https://api.deepseek.com/"
    private const val API_KEY = "sk-67fa91bd38d142c48b3522376beb1f72"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: DeepSeekApiService = retrofit.create(DeepSeekApiService::class.java)

    fun getAuthHeader(): String {
        return "Bearer $API_KEY"
    }
}