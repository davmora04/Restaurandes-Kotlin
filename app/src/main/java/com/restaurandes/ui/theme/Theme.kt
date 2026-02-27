package com.restaurandes.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    secondary = Secondary,
    tertiary = Accent,
    background = TextPrimary,
    surface = Color(0xFF3A3632),
    onPrimary = BackgroundWhite,
    onSecondary = TextPrimary,
    onTertiary = BackgroundWhite,
    onBackground = BackgroundWhite,
    onSurface = BackgroundWhite,
    error = StatusClosed,
    onError = BackgroundWhite
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    secondary = Secondary,
    tertiary = Accent,
    background = Background,
    surface = Surface,
    onPrimary = BackgroundWhite,
    onSecondary = TextPrimary,
    onTertiary = BackgroundWhite,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    primaryContainer = Color(0xFFFFE4D6),
    onPrimaryContainer = Primary,
    secondaryContainer = Color(0xFFFFF0E5),
    onSecondaryContainer = TextPrimary,
    error = StatusClosed,
    onError = BackgroundWhite,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = StatusClosed,
    outline = BorderMedium,
    outlineVariant = BorderLight
)

@Composable
fun RestaurandesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Desactivado para usar colores personalizados
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
