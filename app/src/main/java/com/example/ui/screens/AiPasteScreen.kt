package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.viewmodel.TranscriptViewModel
import com.example.ui.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiPasteScreen(
    onParsed: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: TranscriptViewModel = viewModel()
) {
    var textToParse by remember { mutableStateOf("") }
    val parseState by viewModel.parseState.collectAsState()

    LaunchedEffect(parseState) {
        if (parseState is UiState.Success) {
            val transcript = (parseState as UiState.Success).data
            viewModel.saveTranscript(transcript)
            viewModel.resetParseState()
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Transcript Paste") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Paste your transcript or result data below. AI will automatically structure it for you.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = textToParse,
                onValueChange = { textToParse = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                label = { Text("Paste data here...") },
                placeholder = { Text("Example:\nName: John Doe\nSubject: Math, Marks: 85...") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            if (parseState is UiState.Loading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = { viewModel.parseText(textToParse) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = textToParse.isNotBlank()
                ) {
                    Text("Structure with AI")
                }
            }
            
            if (parseState is UiState.Error) {
                Text(
                    text = (parseState as UiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
