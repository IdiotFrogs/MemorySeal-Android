package com.idiotfrogs.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Primary
private val PrimaryLight = Color(0xFF9EE5B0)
private val PrimaryNormal = Color(0xFF34C759)
private val PrimaryDark = Color(0xFF2EAE4D)

// Grey
private val GreyG1 = Color(0xFFE6E6E6)
private val GreyG2 = Color(0xFFD2D3D6)
private val GreyG3 = Color(0xFF919191)
private val GreyG4 = Color(0xFF454545)
private val GreyG5 = Color(0xFF1A1A1A)

// Background
private val BGNormal = Color(0xFFF5F5F5)

// System
private val Red = Color(0xFFDD0303)
private val Blue = Color(0xFF5C8EED)

// Static
private val Black = Color(0xFF000000)
private val White = Color(0xFFFFFFFF)

@Immutable
data class MSColor(
    // primary
    val primaryLight: Color = PrimaryLight,
    val primaryNormal: Color = PrimaryNormal,
    val primaryDark: Color = PrimaryDark,

    // background
    val bgNormal: Color = BGNormal,

    // grey
    val greyG1: Color = GreyG1,
    val greyG2: Color = GreyG2,
    val greyG3: Color = GreyG3,
    val greyG4: Color = GreyG4,
    val greyG5: Color = GreyG5,

    // system
    val red: Color = Red,
    val blue: Color = Blue,

    // static
    val black: Color = Black,
    val white: Color = White,
)

val LocalMSColor = staticCompositionLocalOf { MSColor() }