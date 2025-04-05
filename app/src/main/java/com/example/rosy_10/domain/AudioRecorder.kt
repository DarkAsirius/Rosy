package com.example.rosy_10.domain

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class AudioRecorder(private val context: Context) {
    private var audioRecord: AudioRecord? = null
    private var recordingThread: Thread? = null
    private var isRecording = false
    private lateinit var outputFile: File

    @Throws(IOException::class, SecurityException::class)
    fun startRecording(): File {
        // Создаем временный PCM-файл
        val pcmFile = File(context.externalCacheDir, "temp_recording_${System.currentTimeMillis()}.pcm")
        outputFile = File(context.externalCacheDir, "recording_${System.currentTimeMillis()}.wav") // Финальный WAV-файл

        val sampleRate = 44100
        val channelConfig = AudioFormat.CHANNEL_IN_MONO
        val audioFormat = AudioFormat.ENCODING_PCM_16BIT
        val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            bufferSize
        ).apply {
            startRecording()
        }

        isRecording = true

        recordingThread = Thread {
            val data = ByteArray(bufferSize)
            FileOutputStream(pcmFile).use { fos ->
                while (isRecording) {
                    val read = audioRecord?.read(data, 0, bufferSize) ?: 0
                    if (read > 0) {
                        fos.write(data, 0, read)
                    }
                }
            }
            // Конвертируем PCM в WAV после записи
            convertPcmToWav(pcmFile, outputFile)
            pcmFile.delete() // Удаляем временный PCM-файл
        }.apply { start() }

        return outputFile
    }

    fun stopRecording() {
        isRecording = false
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
        recordingThread?.join()
        recordingThread = null
    }

    // Конвертация PCM в WAV
    private fun convertPcmToWav(pcmFile: File, wavFile: File) {
        val sampleRate = 44100
        val channels = 1
        val bitsPerSample = 16

        val pcmData = pcmFile.readBytes()
        val wavStream = FileOutputStream(wavFile)

        // Записываем WAV-заголовок
        writeWavHeader(wavStream, pcmData.size.toLong(), sampleRate, channels, bitsPerSample)
        wavStream.write(pcmData)
        wavStream.close()
    }

    // Запись заголовка WAV
    private fun writeWavHeader(
        out: FileOutputStream,
        pcmDataSize: Long,
        sampleRate: Int,
        channels: Int,
        bitsPerSample: Int
    ) {
        val byteRate = sampleRate * channels * bitsPerSample / 8
        val blockAlign = channels * bitsPerSample / 8
        val totalDataSize = pcmDataSize + 36 // 36 — размер заголовка WAV

        // Записываем заголовок
        out.write("RIFF".toByteArray()) // ChunkID
        out.write(intToByteArray(totalDataSize.toInt()), 0, 4) // ChunkSize
        out.write("WAVE".toByteArray()) // Format
        out.write("fmt ".toByteArray()) // Subchunk1ID
        out.write(intToByteArray(16), 0, 4) // Subchunk1Size (16 для PCM)
        out.write(shortToByteArray(1), 0, 2) // AudioFormat (1 = PCM)
        out.write(shortToByteArray(channels), 0, 2) // NumChannels
        out.write(intToByteArray(sampleRate), 0, 4) // SampleRate
        out.write(intToByteArray(byteRate), 0, 4) // ByteRate
        out.write(shortToByteArray(blockAlign), 0, 2) // BlockAlign
        out.write(shortToByteArray(bitsPerSample), 0, 2) // BitsPerSample
        out.write("data".toByteArray()) // Subchunk2ID
        out.write(intToByteArray(pcmDataSize.toInt()), 0, 4) // Subchunk2Size
    }

    // Вспомогательные функции для конвертации чисел в байты
    private fun intToByteArray(i: Int): ByteArray {
        return byteArrayOf(
            (i and 0xFF).toByte(),
            ((i shr 8) and 0xFF).toByte(),
            ((i shr 16) and 0xFF).toByte(),
            ((i shr 24) and 0xFF).toByte()
        )
    }

    private fun shortToByteArray(s: Int): ByteArray {
        return byteArrayOf(
            (s and 0xFF).toByte(),
            ((s shr 8) and 0xFF).toByte()
        )
    }
}