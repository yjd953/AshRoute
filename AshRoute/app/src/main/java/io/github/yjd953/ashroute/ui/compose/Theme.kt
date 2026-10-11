package io.github.yjd953.ashroute.ui.compose

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import io.github.yjd953.ashroute.AppConfig
import io.github.yjd953.ashroute.handler.MmkvManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private val LightColor = lightColorScheme(
    primary = Color(0xFF1B1B1D), // Graphite
    onPrimary = Color(0xFFFFFFFF), // White
    primaryContainer = Color(0xFFE7E5E4), // Light Graphite
    onPrimaryContainer = Color(0xFF1B1B1D), // Graphite
    secondary = Color(0xFFC63C32), // Vermilion
    onSecondary = Color(0xFFFFFFFF), // White
    secondaryContainer = Color(0xFFFFE3DF), // Pale Vermilion
    onSecondaryContainer = Color(0xFF3F0805), // Deep Vermilion
    tertiary = Color(0xFF496453), // Muted Green
    onTertiary = Color(0xFFFFFFFF), // White
    tertiaryContainer = Color(0xFFCBEAD5), // Light Green
    onTertiaryContainer = Color(0xFF062114), // Deep Green
    error = Color(0xFFBA1A1A), // Red
    errorContainer = Color(0xFFFFDAD6), // Light Red
    onError = Color(0xFFFFFFFF), // White
    onErrorContainer = Color(0xFF410002), // Dark Red
    background = Color(0xFFFCFAF9), // Warm White
    onBackground = Color(0xFF1C1B1A), // Near Black
    surface = Color(0xFFFCFAF9), // Warm White
    onSurface = Color(0xFF1C1B1A), // Near Black
    surfaceVariant = Color(0xFFF0ECEA), // Warm Gray
    onSurfaceVariant = Color(0xFF5D5754), // Dark Gray
    outline = Color(0xFF817873), // Medium Gray
    outlineVariant = Color(0xFFD7CFCA), // Light Gray
    inverseSurface = Color(0xFF312E2C), // Dark Gray
    inverseOnSurface = Color(0xFFF7F3F1), // Very Light Gray
    inversePrimary = Color(0xFFD7D2CF), // Silver Gray
    scrim = Color(0xFF000000), // Black
    surfaceTint = Color(0xFF000000), // Black
    surfaceContainerLowest = Color(0xFFFFFFFF), // White
    surfaceContainerLow = Color(0xFFF7F4F2), // Very Light Gray
    surfaceContainer = Color(0xFFF1EEEC), // Light Gray
    surfaceContainerHigh = Color(0xFFEBE7E4), // Light Gray
    surfaceContainerHighest = Color(0xFFE5E0DD), // Light Gray
)

private val DarkColor = darkColorScheme(
    primary = Color(0xFFD6D1CE), // Silver Gray
    onPrimary = Color(0xFF292625), // Dark Gray
    primaryContainer = Color(0xFF474240), // Gray
    onPrimaryContainer = Color(0xFFEDE8E5), // Light Gray
    secondary = Color(0xFFFFADA4), // Light Vermilion
    onSecondary = Color(0xFF650F0B), // Deep Vermilion
    secondaryContainer = Color(0xFF8F211A), // Dark Vermilion
    onSecondaryContainer = Color(0xFFFFDAD5), // Pale Vermilion
    tertiary = Color(0xFFAFD1BA), // Muted Green
    onTertiary = Color(0xFF1B3827), // Dark Green
    tertiaryContainer = Color(0xFF324F3D), // Green
    onTertiaryContainer = Color(0xFFCBEAD5), // Light Green
    error = Color(0xFFFFB4AB), // Light Red
    errorContainer = Color(0xFF93000A), // Dark Red
    onError = Color(0xFF690005), // Deep Red
    onErrorContainer = Color(0xFFFFDAD6), // Light Red
    background = Color(0xFF171615), // Near Black
    onBackground = Color(0xFFE9E3E0), // Light Gray
    surface = Color(0xFF171615), // Near Black
    onSurface = Color(0xFFE9E3E0), // Light Gray
    surfaceVariant = Color(0xFF504A47), // Dark Gray
    onSurfaceVariant = Color(0xFFD5CCC7), // Light Gray
    outline = Color(0xFF9C918B), // Medium Gray
    outlineVariant = Color(0xFF504A47), // Dark Gray
    inverseSurface = Color(0xFFE9E3E0), // Light Gray
    inverseOnSurface = Color(0xFF312E2C), // Near Black
    inversePrimary = Color(0xFF1B1B1D), // Graphite
    scrim = Color(0xFF000000), // Black
    surfaceTint = Color(0xFFD6D1CE), // Silver Gray
    surfaceContainerLowest = Color(0xFF100F0F), // Near Black
    surfaceContainerLow = Color(0xFF1C1A19), // Dark Gray
    surfaceContainer = Color(0xFF211F1E), // Dark Gray
    surfaceContainerHigh = Color(0xFF2B2827), // Dark Gray
    surfaceContainerHighest = Color(0xFF373331), // Dark Gray
)

// Semantic Colors
val colorPing = Color(0xFF009966) // Green
val colorPingRed = Color(0xFFC63C32) // Vermilion
val colorConfigType = Color(0xFFC63C32) // Vermilion
val colorFabActive = Color(0xFFC63C32) // Vermilion
val colorFabInactiveLight = Color(0xFF9C9C9C) // Gray
val colorFabInactiveDark = Color(0xFF646464) // Dark Gray
val dividerColorLight = Color(0xFFE0E0E0) // Light Gray
val dividerColorDark = Color(0xFF424242) // Dark Gray

// Toast Colors 85%
val toastNormalBgLight = Color(0xD9353A3E) // Dark Gray
val toastNormalBgDark = Color(0xD94A4F54) // Darker Gray
val toastSuccessBg = Color(0xD9388E3C) // Green
val toastErrorBg = Color(0xD9D50000) // Red
val toastInfoBg = Color(0xD93F51B5) // Indigo Blue
val toastIconCircleBg = Color(0x33FFFFFF) // Semi-transparent White
val toastTextColor = Color.White // White

object ThemeManager {
    private val _themeMode = MutableStateFlow(
        MmkvManager.decodeSettingsString(AppConfig.PREF_UI_MODE_NIGHT, "0") ?: "0"
    )
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val _dynamicColorEnabled = MutableStateFlow(
        MmkvManager.decodeSettingsBool(AppConfig.PREF_DYNAMIC_COLOR, false)
    )
    val dynamicColorEnabled: StateFlow<Boolean> = _dynamicColorEnabled.asStateFlow()

    fun setThemeMode(mode: String) {
        MmkvManager.encodeSettings(AppConfig.PREF_UI_MODE_NIGHT, mode)
        _themeMode.value = mode
    }

    fun setDynamicColorEnabled(enabled: Boolean) {
        MmkvManager.encodeSettings(AppConfig.PREF_DYNAMIC_COLOR, enabled)
        _dynamicColorEnabled.value = enabled
    }

    fun refresh() {
        _themeMode.value =
            MmkvManager.decodeSettingsString(AppConfig.PREF_UI_MODE_NIGHT, "0") ?: "0"
        _dynamicColorEnabled.value =
            MmkvManager.decodeSettingsBool(AppConfig.PREF_DYNAMIC_COLOR, false)
    }
}

@Composable
fun resolveDarkTheme(): Boolean {
    val mode by ThemeManager.themeMode.collectAsState()
    return when (mode) {
        "1" -> false
        "2" -> true
        else -> isSystemInDarkTheme()
    }
}

val LocalDarkTheme = compositionLocalOf { false }

@Composable
fun AppTheme(
    darkTheme: Boolean = resolveDarkTheme(),
    content: @Composable () -> Unit
) {
    val dynamicColor by ThemeManager.dynamicColorEnabled.collectAsState()
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColor
        else -> LightColor
    }
    val snackbarController = rememberAppSnackbarController()

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context as? Activity ?: return@SideEffect
            val window = activity.window
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(
        LocalDarkTheme provides darkTheme,
        LocalAppSnackbar provides snackbarController
    ) {
        MaterialTheme(
            colorScheme = colorScheme
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AppSnackbarBridge(controller = snackbarController)
                content()
                AppSnackbarHost(hostState = snackbarController.hostState)
            }
        }
    }
}
