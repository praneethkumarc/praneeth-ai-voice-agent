package com.example.data

import com.example.BuildConfig
import com.example.model.AiEmployee
import com.example.model.TranscriptMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiVoiceService {

  private val client = OkHttpClient.Builder()
    .connectTimeout(15, TimeUnit.SECONDS)
    .readTimeout(20, TimeUnit.SECONDS)
    .build()

  /**
   * Generates a conversational voice agent response.
   * If an API key is available in BuildConfig, queries Gemini REST API (gemini-3.5-flash).
   * Otherwise, provides realistic conversational qualification responses.
   */
  suspend fun generateResponse(
    aiEmployee: AiEmployee,
    conversationHistory: List<TranscriptMessage>,
    userUtterance: String
  ): String = withContext(Dispatchers.IO) {
    val apiKey = try {
      BuildConfig.GEMINI_API_KEY
    } catch (e: Exception) {
      ""
    }

    if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY" && !MockRepository.isDemoMode.value) {
      try {
        val model = "gemini-3.5-flash"
        val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"

        val systemInstruction = """
          You are ${aiEmployee.name}, an AI voice employee working for ${aiEmployee.businessName} in the ${aiEmployee.industry} industry.
          Persona: ${aiEmployee.personality}.
          Role/Instructions: ${aiEmployee.jobDescription}.
          Language: ${aiEmployee.language}.
          Keep your spoken responses short, natural, empathetic, and spoken-like (under 2 sentences).
          Your goal is to answer customer questions, gather budget/location/timeline, and qualify them for a meeting or site visit.
        """.trimIndent()

        val contentsArray = JSONArray()

        // Add history
        conversationHistory.takeLast(6).forEach { msg ->
          val role = if (msg.sender == "AI") "model" else "user"
          val partObj = JSONObject().put("text", msg.text)
          val itemObj = JSONObject()
            .put("role", role)
            .put("parts", JSONArray().put(partObj))
          contentsArray.put(itemObj)
        }

        // Add latest user message
        val userItem = JSONObject()
          .put("role", "user")
          .put("parts", JSONArray().put(JSONObject().put("text", userUtterance)))
        contentsArray.put(userItem)

        val rootJson = JSONObject().apply {
          put("contents", contentsArray)
          put(
            "system_instruction",
            JSONObject().put("parts", JSONArray().put(JSONObject().put("text", systemInstruction)))
          )
        }

        val requestBody = rootJson.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder().url(url).post(requestBody).build()
        val response = client.newCall(request).execute()

        if (response.isSuccessful) {
          val responseBody = response.body?.string() ?: ""
          val json = JSONObject(responseBody)
          val text = json.getJSONArray("candidates")
            .getJSONObject(0)
            .getJSONObject("content")
            .getJSONArray("parts")
            .getJSONObject(0)
            .getString("text")
          if (text.isNotBlank()) {
            return@withContext text.trim()
          }
        }
      } catch (e: Exception) {
        // Fallback to intelligent local rules
      }
    }

    // Realistic spoken AI conversational logic
    delay(750) // simulate natural speech synthesis latency
    val lower = userUtterance.lowercase()
    when {
      lower.contains("hello") || lower.contains("hi") || lower.contains("hey") -> {
        aiEmployee.greeting.ifBlank {
          "Hello! This is ${aiEmployee.name} from ${aiEmployee.businessName}. How can I help you today?"
        }
      }
      lower.contains("price") || lower.contains("budget") || lower.contains("cost") || lower.contains("how much") -> {
        "Our configurations start from ₹85 Lakhs up to ₹2.5 Crores for premium units. What budget bracket works best for your family?"
      }
      lower.contains("location") || lower.contains("where") || lower.contains("address") -> {
        "We are conveniently situated in the IT corridor with rapid 10-minute access to the Financial District. Which area are you currently residing in?"
      }
      lower.contains("visit") || lower.contains("appointment") || lower.contains("saturday") || lower.contains("sunday") || lower.contains("meet") -> {
        "Wonderful! I can schedule an exclusive walkthrough with our senior advisor this Saturday or Sunday morning. Which time slot suits you better?"
      }
      lower.contains("bhk") || lower.contains("bedroom") || lower.contains("flat") || lower.contains("villa") -> {
        "We have spacious 2BHK, 3BHK, and 4BHK floor plans available. Are you looking to move in right away or within the next 6 to 12 months?"
      }
      lower.contains("yes") || lower.contains("sure") || lower.contains("okay") -> {
        "Perfect! I have recorded your preferences and I'm sending the complete brochure and location map right now to your WhatsApp."
      }
      else -> {
        "I understand. We can certainly accommodate that. Let me note that down and arrange a priority callback with our specialist for you."
      }
    }
  }
}
