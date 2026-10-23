package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.Transcript
import com.example.ui.viewmodel.TranscriptViewModel
import com.example.ui.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTranscriptScreen(
    transcriptId: String?,
    onBack: () -> Unit,
    viewModel: TranscriptViewModel = viewModel()
) {
    val transcriptsState by viewModel.transcriptsState.collectAsState()
    val initialTranscript = if (transcriptId != null) {
        (transcriptsState as? UiState.Success)?.data?.find { it.id == transcriptId }
    } else {
        Transcript()
    }

    var transcript by remember { mutableStateOf(initialTranscript ?: Transcript()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (transcriptId == null) "New Transcript" else "Edit Transcript") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.saveTranscript(transcript)
                        onBack()
                    }) {
                        Icon(Icons.Default.Save, contentDescription = "Save")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = transcript.studentName,
                onValueChange = { transcript = transcript.copy(studentName = it) },
                label = { Text("Student Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = transcript.schoolName,
                onValueChange = { transcript = transcript.copy(schoolName = it) },
                label = { Text("School Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = transcript.studentId,
                    onValueChange = { transcript = transcript.copy(studentId = it) },
                    label = { Text("ID") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = transcript.session,
                    onValueChange = { transcript = transcript.copy(session = it) },
                    label = { Text("Session") },
                    modifier = Modifier.weight(1f)
                )
            }
            
            Text("Subjects (Edit via AI Paste for best experience)", style = MaterialTheme.typography.titleMedium)
            
            transcript.subjects.forEachIndexed { index, subject ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(subject.name, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        Text("Marks: ${subject.marks} | Grade: ${subject.grade} (${subject.gradePoint})")
                    }
                }
            }
            
            Button(
                onClick = { /* Add manual subject logic if needed */ },
                modifier = Modifier.fillMaxWidth(),
                enabled = false // Placeholder
            ) {
                Text("Manual Edit coming soon")
            }
        }
    }
}
