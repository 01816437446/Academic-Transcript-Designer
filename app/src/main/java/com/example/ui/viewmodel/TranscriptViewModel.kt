package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.TranscriptParser
import com.example.data.model.Transcript
import com.example.data.repository.TranscriptRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
    data object Idle : UiState<Nothing>
}

class TranscriptViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TranscriptRepository(application)
    private val parser = TranscriptParser()

    private val _transcriptsState = MutableStateFlow<UiState<List<Transcript>>>(UiState.Loading)
    val transcriptsState: StateFlow<UiState<List<Transcript>>> = _transcriptsState.asStateFlow()

    private val _parseState = MutableStateFlow<UiState<Transcript>>(UiState.Idle)
    val parseState: StateFlow<UiState<Transcript>> = _parseState.asStateFlow()

    init {
        observeTranscripts()
    }

    private fun observeTranscripts() {
        repository.observeTranscripts()
            .onEach { _transcriptsState.value = UiState.Success(it) }
            .catch { _transcriptsState.value = UiState.Error(it.localizedMessage ?: "Failed to load transcripts") }
            .launchIn(viewModelScope)
    }

    fun parseText(text: String) {
        viewModelScope.launch {
            _parseState.value = UiState.Loading
            try {
                val transcript = parser.parseTranscriptText(text)
                _parseState.value = UiState.Success(transcript)
            } catch (e: Exception) {
                _parseState.value = UiState.Error(e.localizedMessage ?: "Parsing failed")
            }
        }
    }

    fun saveTranscript(transcript: Transcript) {
        viewModelScope.launch {
            try {
                repository.saveTranscript(transcript)
            } catch (e: Exception) {
            }
        }
    }

    fun deleteTranscript(id: String) {
        viewModelScope.launch {
            try {
                repository.deleteTranscript(id)
            } catch (e: Exception) {
            }
        }
    }
    
    fun resetParseState() {
        _parseState.value = UiState.Idle
    }
}
