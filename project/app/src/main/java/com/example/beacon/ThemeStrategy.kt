package com.example.beacon

import androidx.compose.ui.graphics.Color
import com.example.beacon.ui.theme.DarkThemePrimaryBlack
import com.example.beacon.ui.theme.DarkThemeSecondaryGrey
import com.example.beacon.ui.theme.DarkThemeTextWhite
import com.example.beacon.ui.theme.DarkThemeTextYellow
import com.example.beacon.ui.theme.LightThemePrimaryYellow
import com.example.beacon.ui.theme.LightThemeSecondaryBlack
import com.example.beacon.ui.theme.LightThemeTextBlack
import com.example.beacon.ui.theme.LightThemeTextWhite

interface ThemeStrategy {
    val primaryColor: Color
    val secondaryColor: Color
    val primaryTextColor: Color
    val secondaryTextColor: Color
}

object LightThemeStrategy : ThemeStrategy {
    override val primaryColor = LightThemePrimaryYellow
    override val secondaryColor = LightThemeSecondaryBlack
    override val primaryTextColor = LightThemeTextBlack
    override val secondaryTextColor = LightThemeTextWhite
}

object DarkThemeStrategy : ThemeStrategy {
    override val primaryColor = DarkThemePrimaryBlack
    override val secondaryColor = DarkThemeSecondaryGrey
    override val primaryTextColor = DarkThemeTextYellow
    override val secondaryTextColor = DarkThemeTextWhite
}