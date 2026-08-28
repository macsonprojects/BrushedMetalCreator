package com.example.brushedmetalcreator.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

@Immutable
data class BrushedMetalColors(
    val background: Color,
    val drawerBorder: Color,
    val swipeZoneBorderActive: Color,
    val swipeZoneBorderInactive: Color,
    val snackbarBackground: Color,
    val iconActive: Color,
    val iconInactive: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val menuBackgroundDefault: Color,
    val menuBackgroundActive: Color,
    val menuBackgroundReset: Color,
    val menuBackgroundResetPressed: Color,
    val menuValueDefault: Color,
    val menuValueActive: Color,
    val menuValueReset: Color,
    val menuValueResetPressed: Color,
    val menuLabelReset: Color,
    val textHyperlink: Color,
)

val LocalBrushedMetalColors = staticCompositionLocalOf {
    BrushedMetalColors(
        background = IndustrialBlack,
        drawerBorder = BorderSubtle,
        swipeZoneBorderActive = BorderActive,
        swipeZoneBorderInactive = BorderInactive,
        snackbarBackground = CharcoalGray,
        iconActive = Color.White,
        iconInactive = SoftWhite,
        textPrimary = Color.White,
        textSecondary = Color.LightGray,
        menuBackgroundDefault = MenuBackgroundDefault,
        menuBackgroundActive = MenuBackgroundActive,
        menuBackgroundReset = MenuBackgroundReset,
        menuBackgroundResetPressed = MenuBackgroundResetPressed,
        menuValueDefault = MenuValueDefault,
        menuValueActive = MenuValueActive,
        menuValueReset = MenuValueReset,
        menuValueResetPressed = MenuValueResetPressed,
        menuLabelReset = MenuLabelReset,
        textHyperlink = TextHyperlink,
    )
}

object BrushedMetalTheme {
    val colors: BrushedMetalColors
        @Composable
        @ReadOnlyComposable
        get() = LocalBrushedMetalColors.current
}

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = IndustrialBlack,
    surface = IndustrialBlack,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun BrushedMetalCreatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic colour is available on Android 12+
    dynamicColor: Boolean = true,
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

    val brushedMetalColors = BrushedMetalColors(
        background = IndustrialBlack,
        drawerBorder = BorderSubtle,
        swipeZoneBorderActive = BorderActive,
        swipeZoneBorderInactive = BorderInactive,
        snackbarBackground = CharcoalGray,
        iconActive = Color.White,
        iconInactive = MutedGray,
        textPrimary = Color.White,
        textSecondary = Color.LightGray,
        menuBackgroundDefault = MenuBackgroundDefault,
        menuBackgroundActive = MenuBackgroundActive,
        menuBackgroundReset = MenuBackgroundReset,
        menuBackgroundResetPressed = MenuBackgroundResetPressed,
        menuValueDefault = MenuValueDefault,
        menuValueActive = MenuValueActive,
        menuValueReset = MenuValueReset,
        menuValueResetPressed = MenuValueResetPressed,
        menuLabelReset = MenuLabelReset,
        textHyperlink = TextHyperlink,
    )

    CompositionLocalProvider(LocalBrushedMetalColors provides brushedMetalColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
