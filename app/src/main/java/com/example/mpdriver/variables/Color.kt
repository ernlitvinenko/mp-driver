package com.example.mpdriver.variables

import androidx.compose.ui.graphics.Color


sealed class JDEColor(val color: Color) {
    data object PRIMARY: JDEColor(Color(0xFFE5332A))
    data object SUCCESS: JDEColor(Color(0xFF45900B))
    data object WARNING: JDEColor(Color(0xFFFFC700))
    data object SECONDARY: JDEColor(Color.Gray)
    data object BLACK: JDEColor(Color.Black)
    data object BG_GRAY: JDEColor(Color(0xFFEEEEEE))
    data object TEXT_FIELD_BG_COLOR: JDEColor(Color(0xFFF2F2F2))
    data object TEXT_FOCUSED_FIELD_COLOR: JDEColor(Color(0xFFE2E2E2))
}