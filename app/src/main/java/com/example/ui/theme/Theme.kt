package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LuxuryDarkColorScheme = darkColorScheme(
  primary = GoldPrimary,
  onPrimary = OnyxBlack,
  primaryContainer = GoldContainer,
  onPrimaryContainer = OnGoldContainer,
  secondary = PureWhite,
  onSecondary = OnyxBlack,
  secondaryContainer = CardDark,
  onSecondaryContainer = PureWhite,
  tertiary = GoldMetallic,
  onTertiary = OnyxBlack,
  background = OnyxBlack,
  onBackground = PureWhite,
  surface = DarkCharcoal,
  onSurface = PureWhite,
  surfaceVariant = CardDark,
  onSurfaceVariant = TextMutedDark,
  outline = CardBorderDark,
  outlineVariant = GoldDark
)

private val LuxuryLightColorScheme = lightColorScheme(
  primary = GoldDark,
  onPrimary = PureWhite,
  primaryContainer = GoldLight,
  onPrimaryContainer = OnyxBlack,
  secondary = OnyxBlack,
  onSecondary = PureWhite,
  secondaryContainer = OffWhite,
  onSecondaryContainer = OnyxBlack,
  tertiary = GoldPrimary,
  onTertiary = OnyxBlack,
  background = OffWhite,
  onBackground = OnyxBlack,
  surface = PureWhite,
  onSurface = OnyxBlack,
  surfaceVariant = Color(0xFFF0F0F3),
  onSurfaceVariant = TextMutedLight,
  outline = Color(0xFFD0D0D5),
  outlineVariant = GoldPrimary
)

@Composable
fun MansuriPaintsTheme(
  darkTheme: Boolean = true, // Default to dark luxury theme
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) LuxuryDarkColorScheme else LuxuryLightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}

// Alias for backward compatibility
@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  content: @Composable () -> Unit,
) {
  MansuriPaintsTheme(darkTheme = darkTheme, content = content)
}
