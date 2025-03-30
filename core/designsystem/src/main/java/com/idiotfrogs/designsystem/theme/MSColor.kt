package com.idiotfrogs.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Primary
private val PrimaryLight = Color(0xFF9EE5B0)
private val PrimaryNormal = Color(0xFF34C759)
private val PrimaryDark = Color(0xFF2EAE4D)

// Secondary
private val SecondaryLight = Color(0xFFFAE876)
private val SecondaryNormal = Color(0xFFE0C409)
private val SecondaryDark = Color(0xFFCEB408)

// Grey
private val GreyG1 = Color(0xFFE6E6E6)
private val GreyG2 = Color(0xFFD2D3D6)
private val GreyG3 = Color(0xFF919191)
private val GreyG4 = Color(0xFF454545)
private val GreyG5 = Color(0xFF1A1A1A)

// System
private val Red = Color(0xFFEC5151)
private val Blue = Color(0xFF6393EE)


@Immutable
data class MSColor(
    // primary
    val primaryLight: Color = PrimaryLight,
    val primaryNormal: Color = PrimaryNormal,
    val primaryDark: Color = PrimaryDark,

    // secondary
    val secondaryLight: Color = SecondaryLight,
    val secondaryNormal: Color = SecondaryNormal,
    val secondaryDark: Color = SecondaryDark,

    // grey
    val greyG1: Color = GreyG1,
    val greyG2: Color = GreyG2,
    val greyG3: Color = GreyG3,
    val greyG4: Color = GreyG4,
    val greyG5: Color = GreyG5,

    // system
    val red: Color = Red,
    val blue: Color = Blue,
)

val LocalMSColor = staticCompositionLocalOf { MSColor() }