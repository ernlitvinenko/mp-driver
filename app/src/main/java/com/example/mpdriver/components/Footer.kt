package com.example.mpdriver.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.mpdriver.R
import com.example.mpdriver.variables.Route
import com.example.mpdriver.variables.Routes
import com.example.mpdriver.viewmodels.MainViewModel


private data class FooterNavMenuLabel(
    val title: String,
    val defaultImage: Int,
    val imageActive: Int
)


private data class FooterLinkData(
    val link: Route,
    val label: FooterNavMenuLabel
)

@Preview(showBackground = true)
@Composable
fun Footer(
    modifier: Modifier = Modifier,
    model: MainViewModel = viewModel(),
    navigateTo: (Route) -> Unit = {}
) {

    val menuItems = listOf(
        FooterLinkData(
            Routes.Home.Feed,
            label = FooterNavMenuLabel("Лента", R.drawable.home_default, R.drawable.home)
        ),
        FooterLinkData(
            Routes.Home.Events,
            label = FooterNavMenuLabel("События", R.drawable.calendar_default, R.drawable.calendar)
        ),
        FooterLinkData(
            Routes.Home.Notifications,
            label = FooterNavMenuLabel("Уведомления", R.drawable.bell_default, R.drawable.bell)
        ),
        FooterLinkData(
            Routes.Home.Chat,
            label = FooterNavMenuLabel("Чат", R.drawable.chat_default, R.drawable.chat),
        ),
        FooterLinkData(
            Routes.Home.Map,
            label = FooterNavMenuLabel("Карта", R.drawable.location_default, R.drawable.location)
        )
    )

    val activeRoute = model.activeRoute.observeAsState(Routes.Home.Feed)

    Row(
        modifier = modifier
            .shadow(10.dp, RoundedCornerShape(10.dp))
            .fillMaxWidth()
            .background(Color.White)
            .clip(RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp))
            .padding(horizontal = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            menuItems.forEach {
                TextButton(
                    onClick = {
                        model.setActiveRoute(it.link)
                        navigateTo(it.link)
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.Black,
                    )
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        when (activeRoute.value) {
                            it.link -> {
                                Image(
                                    painter = painterResource(id = it.label.imageActive),
                                    contentDescription = ""
                                )
                            }
                            else ->                             Image(
                                painter = painterResource(id = it.label.defaultImage),
                                contentDescription = ""
                            )

                        }
                        Text(text = it.label.title, fontSize = 11.sp, color = if (activeRoute.value == it.link) Color.Black else Color.Gray)
                    }
                }

            }
        }
    }
}




