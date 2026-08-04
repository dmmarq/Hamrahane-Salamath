package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = TealPrimary,
    onPrimary = Color.White,
    primaryContainer = TealPrimaryContainer,
    onPrimaryContainer = TealOnPrimaryContainer,
    secondary = TealSecondary,
    onSecondary = Color.White,
    secondaryContainer = TealSecondaryContainer,
    onSecondaryContainer = TealOnPrimaryContainer,
    tertiary = TealGold,
    onTertiary = TealTextPrimary,
    tertiaryContainer = TealPrimaryContainer,
    background = TealBackground,
    onBackground = TealTextPrimary,
    surface = TealSurface,
    onSurface = TealTextPrimary,
    surfaceVariant = TealSecondaryContainer,
    onSurfaceVariant = TealTextSecondary,
    outline = TealBorder
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF26C6DA),
    onPrimary = Color(0xFF00363A),
    primaryContainer = Color(0xFF004D40),
    onPrimaryContainer = Color(0xFF80DEEA),
    secondary = Color(0xFF4DB6AC),
    onSecondary = Color(0xFF003732),
    secondaryContainer = Color(0xFF004D44),
    onSecondaryContainer = Color(0xFFB2DFDB),
    tertiary = TealGold,
    onTertiary = Color(0xFF3E2723),
    background = TealDarkBackground,
    onBackground = Color(0xFFE0F2F1),
    surface = TealDarkSurface,
    onSurface = Color(0xFFE0F2F1),
    surfaceVariant = Color(0xFF1E2E2C),
    onSurfaceVariant = Color(0xFFB0BEC5),
    outline = Color(0xFF37474F)
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Set default dynamicColor to false to keep Sleek Interface branding across devices
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
