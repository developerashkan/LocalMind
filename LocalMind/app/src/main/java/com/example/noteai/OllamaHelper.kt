package com.example.noteai

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class OllamaHelper(
    private val client: OkHttpClient = OkHttpClient(),
    private val baseUrl: String = "http://10.0.2.2:11434"
) {
    suspend fun generateSuggestion(prompt: String): Result<String> {
        return runCatching {
            val payload = JSONObject(
                mapOf(
                    "model" to "llama3",
                    "prompt" to prompt,
                    "stream" to false
                )
            )
            val request = Request.Builder()
                .url("$baseUrl/api/generate")
                .post(payload.toString().toRequestBody("application/json".toMediaType()))
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    error("Ollama request failed: ${response.code}")
                }
                val body = response.body?.string().orEmpty()
                val json = JSONObject(body)
                json.optString("response").ifBlank {
                    error("Empty response from Ollama")
                }
            }
        }
    }
}
