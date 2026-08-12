package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SleekDarkColorScheme = darkColorScheme(
  primary = SleekPrimary,
  onPrimary = Color(0xFF381E72),
  primaryContainer = SleekPrimaryContainer,
  onPrimaryContainer = SleekOnPrimaryContainer,
  secondary = SleekSecondary,
  onSecondary = Color(0xFF332D41),
  tertiary = SleekTertiary,
  background = SleekBg,
  onBackground = SleekTextMain,
  surface = SleekCard,
  onSurface = SleekTextMain,
  surfaceVariant = SleekSurfaceVariant,
  onSurfaceVariant = SleekTextMuted,
  outline = Color(0xFF938F96)
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = SleekDarkColorScheme,
    typography = Typography,
    content = content
  )
}

