package com.example.data

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
import com.example.BuildConfig

@JsonClass(generateAdapter = true)
data class Part(val text: String?)

@JsonClass(generateAdapter = true)
data class Content(val parts: List<Part>)

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(val contents: List<Content>)

@JsonClass(generateAdapter = true)
data class Candidate(val content: Content)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(val candidates: List<Candidate>?)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val service: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }
}

class GeminiRepository {
    suspend fun askCopilot(history: List<ChatMessageEntity>, prompt: String): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return "Note: Gemini API key is not configured in Secrets yet. To enable active Copilot responses, please configure GEMINI_API_KEY in the AI Studio Secrets panel.\n\nHere is an offline response from AgriCopilot: As a sustainable agriculture assistant, I can guide you about direct farmer marketplaces, organic compost recipes, or how to register your delivery route! Let me know what you need."
        }

        // Standard pre-configured instructions for AgriCopilot
        val systemInstruction = "You are AgriCopilot, an expert AI advisor for a modern direct Farm-to-Home business ecosystem. You are highly professional, empathetic, and knowledgeable about organic agriculture, fair pricing, route optimization, sustainable packaging, and connecting local farmers directly with consumers. Help clients with recipes or nutritional info, farmers with crop yields, and delivery agents with efficient practices. Keep answers concise, helpful, and highly tailored to farm-to-home logistics."

        val apiContents = mutableListOf<Content>()
        
        // Add system instruction context or direct context
        apiContents.add(Content(listOf(Part(systemInstruction))))
        
        // Add recent message history (limit to last 10 messages to keep within context and minimize latency)
        history.takeLast(10).forEach { msg ->
            val speakerRole = if (msg.isUser) "user" else "model"
            // Wait, standard content role could be set, but since we are sending a direct sequence, let's keep it simple:
            val prefix = if (msg.isUser) "User asks: " else "AgriCopilot replies: "
            apiContents.add(Content(listOf(Part("$prefix${msg.message}"))))
        }

        // Add current user prompt
        apiContents.add(Content(listOf(Part("User asks: $prompt"))))

        val request = GenerateContentRequest(contents = apiContents)

        return try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: "No response from AgriCopilot. Please try again."
        } catch (e: Exception) {
            "Error contacting AgriCopilot: ${e.localizedMessage ?: "Unknown network issue"}. Please verify your network connection and GEMINI_API_KEY configuration."
        }
    }
}
