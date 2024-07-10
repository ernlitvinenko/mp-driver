package com.example.mpdriver.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.mpdriver.components.Layout
import com.example.mpdriver.components.note.NoteComponent


@Preview(showBackground = true)
@Composable
fun NoteScreen() {


    Layout(dataList = (0..5).toMutableList()) {
        NoteComponent()
    }
}