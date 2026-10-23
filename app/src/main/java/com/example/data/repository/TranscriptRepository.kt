package com.example.data.repository

import android.content.Context
import com.example.data.model.Transcript
import com.example.R
import com.example.util.OperationType
import com.example.util.handleFirestoreError
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class TranscriptRepository(private val db: FirebaseFirestore) {
    
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    fun observeTranscripts(): Flow<List<Transcript>> = flow {
        val uid = requireUserId()
        emitAll(
            db.collection("users").document(uid).collection("transcripts")
                .orderBy("createdAt")
                .snapshots()
                .map { snapshot -> snapshot.toObjects(Transcript::class.java) }
        )
    }

    suspend fun saveTranscript(transcript: Transcript) {
        val uid = requireUserId()
        val transcriptsRef = db.collection("users").document(uid).collection("transcripts")
        
        val data = transcript.copy(
            userId = uid,
            updatedAt = null // Trigger @ServerTimestamp
        )

        try {
            if (transcript.id.isBlank()) {
                transcriptsRef.add(data.copy(createdAt = null)).await()
            } else {
                transcriptsRef.document(transcript.id).set(data).await()
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, if (transcript.id.isBlank()) OperationType.CREATE else OperationType.UPDATE, transcriptsRef.path)
            throw e
        }
    }

    suspend fun deleteTranscript(id: String) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("transcripts").document(id)
        try {
            docRef.delete().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            throw e
        }
    }
}
