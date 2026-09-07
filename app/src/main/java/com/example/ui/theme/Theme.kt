package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Flat, Minimalist Light Color Scheme (Zero shadows, clean flat separation)
private val FlatLightColorScheme = lightColorScheme(
    primary = FamilyNavy,
    onPrimary = Color.White,
    primaryContainer = FamilyBlueTint,
    onPrimaryContainer = FamilyNavyDark,
    secondary = FamilyAmber,
    onSecondary = Color.White,
    secondaryContainer = FamilyAmberTint,
    onSecondaryContainer = Color(0xFF78350F),
    tertiary = FamilyEmerald,
    onTertiary = Color.White,
    tertiaryContainer = FamilyEmeraldTint,
    onTertiaryContainer = Color(0xFF064E3B),
    background = Slate50,
    onBackground = Slate900,
    surface = Color.White,
    onSurface = Slate900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate600,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Slate50,
    surfaceContainer = Slate100,
    surfaceContainerHigh = Slate200,
    surfaceContainerHighest = Slate300,
    outline = Color.Transparent,
    outlineVariant = Color.Transparent,
    error = FamilyRed,
    onError = Color.White,
    errorContainer = FamilyRedTint,
    onErrorContainer = Color(0xFF991B1B)
)

// Flat, Minimalist Dark Color Scheme
private val FlatDarkColorScheme = darkColorScheme(
    primary = Color(0xFF60A5FA),
    onPrimary = Slate950,
    primaryContainer = Slate800,
    onPrimaryContainer = Color.White,
    secondary = FamilyAmberLight,
    onSecondary = Slate950,
    secondaryContainer = Color(0xFF451A03),
    onSecondaryContainer = Color(0xFFFDE68A),
    tertiary = FamilyEmeraldLight,
    onTertiary = Slate950,
    tertiaryContainer = Color(0xFF064E3B),
    onTertiaryContainer = Color(0xFFA7F3D0),
    background = Slate950,
    onBackground = Slate50,
    surface = Slate900,
    onSurface = Slate50,
    surfaceVariant = Slate800,
    onSurfaceVariant = Slate400,
    surfaceContainerLowest = Slate950,
    surfaceContainerLow = Slate900,
    surfaceContainer = Slate800,
    surfaceContainerHigh = Slate700,
    surfaceContainerHighest = Slate600,
    outline = Color.Transparent,
    outlineVariant = Color.Transparent,
    error = Color(0xFFF87171),
    onError = Slate950,
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFEE2E2)
)

// Consistent Shapes Scale (Smooth, modern rounded cards & surfaces)
val FlatShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Flat curated minimalist theme
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) FlatDarkColorScheme else FlatLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = FlatShapes,
        content = content
    )
}

