package com.example.mpdriver.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


data class HeaderTabsData(
    val idx: Int,
    val title: String
)

@Composable
fun HeaderTabs(
    modifier: Modifier = Modifier,
    tabsData: List<HeaderTabsData>,
    activeTab: Int = 0,
    setActiveTab: (Int) -> Unit
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(bottom = 15.dp), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        tabsData.forEachIndexed { idx, it ->
            when (activeTab) {
                it.idx -> ActiveButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        setActiveTab(it.idx)
                    },
                    text = it.title
                )
                else -> StaleButton(
                    modifier = Modifier.weight(1f),
                    onClick = { setActiveTab(it.idx) },
                    text = it.title
                )
            }
            if (idx != 0  || idx != tabsData.count() - 1) {
                Spacer(modifier = Modifier.width(10.dp))
            }
        }
    }
}