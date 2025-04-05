package com.example.rosy_10.presentations.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.rosy_10.domain.AudioRecorder
import java.io.File

class AudioViewModel : ViewModel() {
    private var mediaRecorder: MediaRecorder? = null
    private var audioFile: File? = null

    private val _recordingState = MutableStateFlow(false)
    val recordingState: StateFlow<Boolean> = _recordingState.asStateFlow()

    fun startRecording(context: Context) {
        audioFile = File(context.externalCacheDir, "recording_${System.currentTimeMillis()}.mp3")

        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(audioFile?.absolutePath)
            prepare()
            start()
            _recordingState.value = true
        }
    }

    fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
        _recordingState.value = false
    }

    fun playRecording(context: Context) {
        audioFile?.let { file ->
            MediaPlayer().apply {
                setDataSource(file.absolutePath)
                prepare()
                start()
                setOnCompletionListener { release() }
            }
        }
    }
}

sealed class RecordingState {
    object Idle : RecordingState()
    data class Active(val audioFile: File) : RecordingState()
    object Stopped : RecordingState()
}