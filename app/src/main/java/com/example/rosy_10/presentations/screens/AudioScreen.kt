package com.example.rosy_10.presentations.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rosy_10.presentations.viewmodels.AudioViewModel

@Composable
fun AudioScreen() {
    val context = LocalContext.current
    val viewModel: AudioViewModel = viewModel()
    val state by viewModel.recordingState.collectAsState()
    val error by viewModel.errorMessage.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Проверка состояний через when (полностью безопасная)
        when (state) {
            is AudioViewModel.RecordingState.Active -> {
                ActiveContent(
                    onStop = { viewModel.stopRecording() }
                )
            }
            AudioViewModel.RecordingState.Stopped -> {
                StoppedContent(
                    onRestart = { viewModel.startRecording() }
                )
            }
            AudioViewModel.RecordingState.Idle -> {
                IdleContent(
                    onStart = { viewModel.startRecording() }
                )
            }
        }

        ErrorContent(error)
    }
}

@Composable
private fun IdleContent(onStart: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = "Ready to record",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onStart) {
            Text("Start Recording")
        }
    }
}

@Composable
private fun ActiveContent(onStop: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text("Recording...")
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onStop,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Text("Stop Recording")
        }
    }
}

@Composable
private fun StoppedContent(onRestart: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Recording complete",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Recording saved")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRestart) {
            Text("Record Again")
        }
    }
}

@Composable
private fun ErrorContent(message: String?) {
    message?.let {
        Text(
            text = it,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(16.dp)
        )
    }
}