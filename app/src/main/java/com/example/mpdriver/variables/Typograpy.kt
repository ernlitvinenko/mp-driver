package com.example.mpdriver.variables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp


object Typography {
    val TITLE = Title
    val PARAGRAPH = Paragraph
}

object Title {
    @Composable
    fun H1(modifier: Modifier = Modifier, text: String, align: TextAlign = TextAlign.Center) {
        Text(
            text = text,
            modifier.fillMaxWidth(),
            textAlign = align,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp
        )
    }

    @Composable
    fun H2(modifier: Modifier = Modifier, text: String, align: TextAlign = TextAlign.Center) {
        Text(
            text = text,
            modifier.fillMaxWidth(),
            textAlign = align,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp
        )
    }

    @Composable
    fun H3(modifier: Modifier = Modifier, text: String, align: TextAlign = TextAlign.Center) {
        Text(
            text = text,
            modifier.fillMaxWidth(),
            textAlign = align,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
    }

    @Composable
    fun H4(modifier: Modifier = Modifier, text: String, align: TextAlign = TextAlign.Center) {
        Text(
            text = text,
            modifier.fillMaxWidth(),
            textAlign = align,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
    }

    @Composable
    fun H5(modifier: Modifier = Modifier, text: String, align: TextAlign = TextAlign.Center) {
        Text(
            text = text,
            modifier.fillMaxWidth(),
            textAlign = align,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp
        )
    }

    @Composable
    fun H6(modifier: Modifier = Modifier, text: String, align: TextAlign = TextAlign.Center) {
        Text(
            text = text,
            modifier.fillMaxWidth(),
            textAlign = align,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

object Paragraph {
    val BASE = Base()
    val BOLD = Bold()
    val UNDERLINED = Underlined()
    val BOLD_UNDERLINED = BoldUnderLined()
}

open class Base(
    private val fontWeight: FontWeight = FontWeight.Normal,
    private val textDecoration: TextDecoration = TextDecoration.None
) {
    @Composable
    fun P1(modifier: Modifier = Modifier, text: String, align: TextAlign = TextAlign.Center) {
        Text(
            text = text,
            modifier.fillMaxWidth(),
            textAlign = align,
            fontWeight = fontWeight,
            fontSize = 22.sp,
            textDecoration = textDecoration
        )
    }

    @Composable
    fun P2(modifier: Modifier = Modifier, text: String, align: TextAlign = TextAlign.Center) {
        Text(
            text = text,
            modifier.fillMaxWidth(),
            textAlign = align,
            fontWeight = fontWeight,
            fontSize = 21.sp,
            textDecoration = textDecoration
        )
    }

    @Composable
    fun P3(modifier: Modifier = Modifier, text: String, align: TextAlign = TextAlign.Center) {
        Text(
            text = text,
            modifier.fillMaxWidth(),
            textAlign = align,
            fontWeight = fontWeight,
            fontSize = 18.sp,
            textDecoration = textDecoration
        )
    }

    @Composable
    fun P4(modifier: Modifier = Modifier, text: String, align: TextAlign = TextAlign.Center) {
        Text(
            text = text,
            modifier.fillMaxWidth(),
            textAlign = align,
            fontWeight = fontWeight,
            fontSize = 17.sp,
            textDecoration = textDecoration
        )
    }

    @Composable
    fun P5(modifier: Modifier = Modifier, text: String, align: TextAlign = TextAlign.Center) {
        Text(
            text = text,
            modifier.fillMaxWidth(),
            textAlign = align,
            fontWeight = fontWeight,
            fontSize = 16.sp,
            textDecoration = textDecoration
        )
    }

    @Composable
    fun P6(modifier: Modifier = Modifier, text: String, align: TextAlign = TextAlign.Center) {
        Text(
            text = text,
            modifier.fillMaxWidth(),
            textAlign = align,
            fontWeight = fontWeight,
            fontSize = 15.sp,
            textDecoration = textDecoration
        )
    }

    @Composable
    fun P7(modifier: Modifier = Modifier, text: String, align: TextAlign = TextAlign.Center) {
        Text(
            text = text,
            modifier.fillMaxWidth(),
            textAlign = align,
            fontWeight = fontWeight,
            fontSize = 14.sp,
            textDecoration = textDecoration
        )
    }

    @Composable
    fun P8(modifier: Modifier = Modifier, text: String, align: TextAlign = TextAlign.Center) {
        Text(
            text = text,
            modifier.fillMaxWidth(),
            textAlign = align,
            fontWeight = fontWeight,
            fontSize = 13.sp,
            textDecoration = textDecoration
        )
    }

    @Composable
    fun P9(modifier: Modifier = Modifier, text: String, align: TextAlign = TextAlign.Center) {
        Text(
            text = text,
            modifier.fillMaxWidth(),
            textAlign = align,
            fontWeight = fontWeight,
            fontSize = 12.sp,
            textDecoration = textDecoration
        )
    }

}

class Bold() : Base(fontWeight = FontWeight.Bold)
class Underlined() : Base(textDecoration = TextDecoration.Underline)
class BoldUnderLined() :
    Base(textDecoration = TextDecoration.Underline, fontWeight = FontWeight.Bold)