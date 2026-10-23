package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.Transcript
import com.example.ui.viewmodel.TranscriptViewModel
import com.example.ui.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewScreen(
    transcriptId: String,
    onBack: () -> Unit,
    viewModel: TranscriptViewModel = viewModel()
) {
    val transcriptsState by viewModel.transcriptsState.collectAsState()
    val transcript = (transcriptsState as? UiState.Success)?.data?.find { it.id == transcriptId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transcript Preview") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (transcript == null) {
            Box(modifier = Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                TranscriptSheet(transcript = transcript)
            }
        }
    }
}

@Composable
fun TranscriptSheet(transcript: Transcript) {
    val themeColor = when (transcript.theme) {
        "emerald" -> Color(0xFF10B981)
        "navy" -> Color(0xFF1E3A8A)
        "maroon" -> Color(0xFF8B0000)
        "teal" -> Color(0xFF008080)
        "purple" -> Color(0xFF800080)
        else -> Color(0xFF10B981)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(0.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = transcript.schoolName.uppercase(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = themeColor
                    )
                    Text(
                        text = transcript.schoolAddress,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(themeColor.copy(alpha = 0.1f))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("LOGO", fontSize = 10.sp, color = themeColor)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "OFFICIAL ACADEMIC TRANSCRIPT",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = themeColor
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                InfoColumn("NAME", transcript.studentName, Modifier.weight(1f))
                InfoColumn("ID", transcript.studentId, Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                InfoColumn("ROLL", transcript.studentRoll, Modifier.weight(1f))
                InfoColumn("SESSION", transcript.session, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(32.dp))

            TableHeader(themeColor)
            transcript.subjects.forEach { subject ->
                TableRow(subject)
            }

            Spacer(modifier = Modifier.height(32.dp))

            val totalCredits = transcript.subjects.sumOf { it.credits }
            val totalPoints = transcript.subjects.sumOf { it.gradePoint * it.credits }
            val cgpa = if (totalCredits > 0) totalPoints / totalCredits else 0.0

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(themeColor.copy(alpha = 0.05f))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("TOTAL CREDITS: $totalCredits", fontWeight = FontWeight.Bold)
                Text("CGPA: ${String.format("%.2f", cgpa)}", fontWeight = FontWeight.Black, color = themeColor, fontSize = 20.sp)
            }
        }
    }
}

@Composable
fun InfoColumn(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = label, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun TableHeader(color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color)
            .padding(8.dp)
    ) {
        Text("SUBJECT", modifier = Modifier.weight(3f), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Text("MARKS", modifier = Modifier.weight(1f), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Text("GRADE", modifier = Modifier.weight(1f), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Text("POINT", modifier = Modifier.weight(1f), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TableRow(subject: com.example.data.model.Subject) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(subject.name, modifier = Modifier.weight(3f), fontSize = 12.sp)
        Text(subject.marks.toInt().toString(), modifier = Modifier.weight(1f), fontSize = 12.sp)
        Text(subject.grade, modifier = Modifier.weight(1f), fontSize = 12.sp)
        Text(String.format("%.2f", subject.gradePoint), modifier = Modifier.weight(1f), fontSize = 12.sp)
    }
    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
}
