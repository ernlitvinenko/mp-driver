package com.example.mpdriver.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun <T> Layout(modifier: Modifier = Modifier, header: @Composable () -> Unit = {}, dataList: List<T>, state : LazyListState = rememberLazyListState(), itemComponent: @Composable (T) -> Unit) {
    LazyColumn(
        modifier
            .fillMaxWidth()
            .padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp), state = state) {
        item {
            header()
        }
        items(items = dataList) {
            itemComponent(it)
        }
    }
}