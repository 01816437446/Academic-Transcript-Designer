package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.serialization.Serializable

@IgnoreExtraProperties
data class Transcript(
    @DocumentId val id: String = "",
    val userId: String = "",
    val studentName: String = "",
    val studentId: String = "",
    val studentRoll: String = "",
    val session: String = "",
    val schoolName: String = "",
    val schoolAddress: String = "",
    val schoolLogoUrl: String = "",
    val theme: String = "emerald",
    val subjects: List<Subject> = emptyList(),
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Subject(
    val name: String = "",
    val marks: Double = 0.0,
    val grade: String = "",
    val gradePoint: Double = 0.0,
    val credits: Double = 1.0
)
