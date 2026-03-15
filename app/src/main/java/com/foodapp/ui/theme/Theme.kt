package com.foodapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import com.foodapp.ui.AccentGold
import com.foodapp.ui.DarkBackground
import com.foodapp.ui.DarkSurface

private val AppColorScheme = darkColorScheme(
    primary = AccentGold,
    background = DarkBackground,
    surface = DarkSurface
)

@Composable
fun FoodAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        content = content
    )
}
