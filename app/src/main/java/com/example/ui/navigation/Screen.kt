package com.example.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {
    @Serializable
    data object Home : Screen
    
    @Serializable
    data class EditTranscript(val transcriptId: String? = null) : Screen
    
    @Serializable
    data class PreviewTranscript(val transcriptId: String) : Screen
    
    @Serializable
    data object AiPaste : Screen
}
