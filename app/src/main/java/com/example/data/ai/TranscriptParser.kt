package com.example.data.ai

import com.example.data.model.Subject
import com.example.data.model.Transcript
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

class TranscriptParser {
    
    private val model by lazy {
        Firebase.ai(backend = GenerativeBackend.googleAI())
            .generativeModel(
                modelName = "gemini-3.8-flash",
                generationConfig = generationConfig {
                    responseMimeType = "application/json"
                }
            )
    }

    suspend fun parseTranscriptText(text: String): Transcript {
        val prompt = """
            Parse the following academic transcript or result data into structured JSON.
            Return a JSON object with the following fields:
            - studentName: string
            - studentId: string
            - studentRoll: string
            - session: string
            - schoolName: string
            - schoolAddress: string
            - subjects: array of objects with fields:
                - name: string
                - marks: number
                - grade: string
                - gradePoint: number
                - credits: number
            
            If any information is missing, use empty strings or 0.0.
            
            Input Text:
            $text
        """.trimIndent()

        val response = withTimeout(60_000L) {
            model.generateContent(prompt)
        }

        val jsonString = response.text ?: throw IllegalStateException("Model returned empty text.")
        val json = JSONObject(jsonString)

        val subjectsArray = json.optJSONArray("subjects") ?: org.json.JSONArray()
        val subjects = (0 until subjectsArray.length()).map { i ->
            val s = subjectsArray.getJSONObject(i)
            Subject(
                name = s.optString("name"),
                marks = s.optDouble("marks"),
                grade = s.optString("grade"),
                gradePoint = s.optDouble("gradePoint"),
                credits = s.optDouble("credits", 1.0)
            )
        }

        return Transcript(
            studentName = json.optString("studentName"),
            studentId = json.optString("studentId"),
            studentRoll = json.optString("studentRoll"),
            session = json.optString("session"),
            schoolName = json.optString("schoolName"),
            schoolAddress = json.optString("schoolAddress"),
            subjects = subjects
        )
    }
}
