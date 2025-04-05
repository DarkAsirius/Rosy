package com.example.rosy_10.presentations.viewmodels

import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rosy_10.domain.AudioRecorder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class AudioViewModel : ViewModel() {
    private lateinit var audioRecorder: AudioRecorder
    private var mediaPlayer: MediaPlayer? = null

    private val _recordedFile = MutableStateFlow<File?>(null)
    val recordedFile: StateFlow<File?> = _recordedFile.asStateFlow()

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _textFromSpeech = MutableStateFlow<String?>(null)
    val textFromSpeech: StateFlow<String?> = _textFromSpeech.asStateFlow()

    fun initRecorder(context: Context) {
        audioRecorder = AudioRecorder(context)
    }

    fun startRecording() {
        viewModelScope.launch {
            try {
                val file = audioRecorder.startRecording()
                _recordedFile.value = file
                _isRecording.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Recording error: ${e.localizedMessage}"
            }
        }
    }

    fun stopRecording() {
        viewModelScope.launch {
            try {
                audioRecorder.stopRecording()
                _isRecording.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Stop recording error: ${e.localizedMessage}"
            }
        }
    }

    fun playRecording() {
        _recordedFile.value?.let { file ->
            try {
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(file.absolutePath)
                    prepare()
                    start()
                    setOnCompletionListener { stopPlayback() }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Playback error: ${e.localizedMessage}"
            }
        }
    }

    fun stopPlayback() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    // Удаление записи
    fun deleteRecording() {
        _recordedFile.value?.let { file ->
            if (file.exists()) {
                file.delete()
                _recordedFile.value = null
                _textFromSpeech.value = null
            }
        }
    }

    // Преобразование аудио в текст (заглушка, можно подключить Google ML Kit или другой API)
    fun convertToText() {
        viewModelScope.launch {
            try {
                // Здесь можно добавить вызов API для распознавания речи
                // Например:
                // val text = SpeechToTextConverter.convert(_recordedFile.value)
                // _textFromSpeech.value = text
                _textFromSpeech.value = "Текст из аудио (заглушка)"
            } catch (e: Exception) {
                _errorMessage.value = "Speech-to-text error: ${e.localizedMessage}"
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopRecording()
        stopPlayback()
    }
}