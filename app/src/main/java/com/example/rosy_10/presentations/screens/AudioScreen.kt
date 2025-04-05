// AudioScreen.kt
package com.example.rosy_10.presentations.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rosy_10.presentations.viewmodels.AudioViewModel

@Composable
fun AudioScreen() {
    val context = LocalContext.current
    val viewModel: AudioViewModel = viewModel()
    val isRecording by viewModel.isRecording.collectAsState()
    val recordedFile by viewModel.recordedFile.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val textFromSpeech by viewModel.textFromSpeech.collectAsState()

    // Общий модификатор для всех кнопок
    val buttonModifier = Modifier
        .width(250.dp)  // Фиксированная ширина
        .height(56.dp)  // Стандартная высота Material Design

    LaunchedEffect(Unit) {
        viewModel.initRecorder(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isRecording) {
            Button(
                onClick = { viewModel.stopRecording() },
                modifier = buttonModifier,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Icon(Icons.Default.Stop, contentDescription = "Stop")
                Spacer(Modifier.width(8.dp))
                Text("Stop Recording")
            }
        } else {
            Button(
                onClick = { viewModel.startRecording() },
                modifier = buttonModifier,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
            ) {
                Icon(Icons.Default.Mic, contentDescription = "Mic")
                Spacer(Modifier.width(8.dp))
                Text("Start Recording")
            }

            Spacer(Modifier.height(16.dp))

            // Кнопка воспроизведения
            Button(
                onClick = { viewModel.playRecording() },
                modifier = buttonModifier,
                enabled = recordedFile != null
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Play")
                Spacer(Modifier.width(8.dp))
                Text("Play Recording")
            }

            Spacer(Modifier.height(16.dp))

            // Кнопка удаления
            Button(
                onClick = { viewModel.deleteRecording() },
                modifier = buttonModifier,
                enabled = recordedFile != null,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
                Spacer(Modifier.width(8.dp))
                Text("Delete Recording")
            }

            Spacer(Modifier.height(16.dp))

            // Кнопка преобразования в текст
            Button(
                onClick = { viewModel.convertToText() },
                modifier = buttonModifier,
                enabled = recordedFile != null,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
            ) {
                Icon(Icons.Default.TextFields, contentDescription = "Convert")
                Spacer(Modifier.width(8.dp))
                Text("Convert to Text")
            }

            // Отображение распознанного текста
            textFromSpeech?.let { text ->
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Recognized text: $text",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Отображение ошибок
        errorMessage?.let { message ->
            Spacer(Modifier.height(16.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}