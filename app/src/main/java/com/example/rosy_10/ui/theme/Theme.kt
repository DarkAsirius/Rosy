package com.example.rosy_10.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.example.rosy_10.ui.theme.Typography

@Composable
fun YourAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme, // Используйте кастомную схему если нужно
        typography = Typography, // Из файла Typography.kt
        content = content
    )
}