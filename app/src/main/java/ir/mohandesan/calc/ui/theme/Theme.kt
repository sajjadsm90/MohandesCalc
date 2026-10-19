package ir.mohandesan.calc.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Typography

/**
 * Color tokens used by custom UI components outside MaterialTheme.
 */
val LocalAppColors = staticCompositionLocalOf { LightColors }

data class AppColors(
    val keyNumber: Color,
    val keyOperator: Color,
    val keyFunction: Color,
    val keyAccent: Color,
    val historyItem: Color
)

val LightColors = AppColors(
    keyNumber = KeyNumberLight,
    keyOperator = KeyOperatorLight,
    keyFunction = KeyFunctionLight,
    keyAccent = KeyAccentLight,
    historyItem = HistoryBgLight
)

val DarkColors = AppColors(
    keyNumber = KeyNumberDark,
    keyOperator = KeyOperatorDark,
    keyFunction = KeyFunctionDark,
    keyAccent = KeyAccentDark,
    historyItem = HistoryBgDark
)

private val LightScheme = lightColorScheme(
    primary = BrandPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8F0FF),
    onPrimaryContainer = Color(0xFF062468),
    secondary = BrandSecondary,
    onSecondary = Color.White,
    tertiary = BrandAccent,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline
)

private val DarkScheme = darkColorScheme(
    primary = BrandPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF0A2440),
    onPrimaryContainer = Color(0xFFB4D4FF),
    secondary = BrandSecondary,
    onSecondary = Color.White,
    tertiary = BrandAccent,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline
)

/**
 * Typography. We rely on the system's default sans-serif so we don't have to
 * bundle any font binary in the APK. The user can replace this with
 * Vazirmatn (see README "اضافه‌کردن فونت دلخواه").
 */
private val AppFontFamily: FontFamily = FontFamily.SansSerif

private val AppTypography = Typography(
    displayLarge  = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.Medium,   fontSize = 56.sp, lineHeight = 64.sp),
    displayMedium = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.Medium,   fontSize = 40.sp, lineHeight = 48.sp),
    headlineLarge = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 32.sp, lineHeight = 40.sp),
    headlineMedium= TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 24.sp, lineHeight = 32.sp),
    titleLarge    = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 22.sp, lineHeight = 28.sp),
    titleMedium   = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.Medium,   fontSize = 18.sp, lineHeight = 24.sp),
    titleSmall    = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.Medium,   fontSize = 16.sp, lineHeight = 22.sp),
    bodyLarge     = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.Normal,   fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium    = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.Normal,   fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall     = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.Normal,   fontSize = 12.sp, lineHeight = 16.sp),
    labelLarge    = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.Medium,   fontSize = 14.sp, lineHeight = 20.sp),
    labelMedium   = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.Medium,   fontSize = 12.sp, lineHeight = 16.sp),
    labelSmall    = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.Medium,   fontSize = 11.sp, lineHeight = 16.sp)
)

enum class ThemeMode { LIGHT, DARK, SYSTEM }

@Composable
fun MohandesCalcTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val useDark = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    val colors = if (useDark) DarkColors else LightColors
    val scheme = if (useDark) DarkScheme else LightScheme

    CompositionLocalProvider(LocalAppColors provides colors) {
        MaterialTheme(
            colorScheme = scheme,
            typography = AppTypography,
            content = content
        )
    }
}
