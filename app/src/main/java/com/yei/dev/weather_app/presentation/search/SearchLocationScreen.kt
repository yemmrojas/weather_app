package com.yei.dev.weather_app.presentation.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yei.dev.weather_app.R
import com.yei.dev.weather_app.presentation.topBar.WeatherTopBar
import com.yei.dev.weather_app.ui.theme.Purple40
import com.yei.dev.weather_app.ui.theme.Purple700
import com.yei.dev.weather_app.ui.theme.Purple80

@Composable
fun WeatherSearchScreen(
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Purple700)
    ) {
        WeatherTopBar(
            title = "Search Location",
            onBackClick = onBackClick
        )
        SearchBar()
        Spacer(modifier = Modifier.height(16.dp))
        WeatherCardsList()
    }
}

@Composable
private fun SearchBar() {
    TextField(
        value = "",
        onValueChange = { },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        placeholder = {
            Text(
                text = "Search for a city or airport",
                color = Purple700
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Purple700
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Purple80,
            unfocusedContainerColor = Purple40,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
private fun WeatherCardsList() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(3) { index ->
            WeatherCard(
                temperature = when (index) {
                    0 -> "19°"
                    1 -> "20°"
                    else -> "13°"
                },
                highTemp = when (index) {
                    0 -> "H:24°"
                    1 -> "H:21°"
                    else -> "H:16°"
                },
                lowTemp = when (index) {
                    0 -> "L:18°"
                    1 -> "L:-39°"
                    else -> "L:8°"
                },
                city = when (index) {
                    0 -> "Montreal, Canada"
                    1 -> "Toronto, Canada"
                    else -> "Tokyo, Japan"
                },
                condition = when (index) {
                    0 -> "Mid Rain"
                    1 -> "Fast Wind"
                    else -> "Showers"
                }
            )
        }
    }
}

@Composable
private fun WeatherCard(
    temperature: String,
    highTemp: String,
    lowTemp: String,
    city: String,
    condition: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Purple40
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            TemperatureInfo(
                temperature = temperature,
                highTemp = highTemp,
                lowTemp = lowTemp,
                city = city
            )
            WeatherConditionInfo(condition = condition)
        }
    }
}

@Composable
private fun TemperatureInfo(
    temperature: String,
    highTemp: String,
    lowTemp: String,
    city: String
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = temperature,
            color = Color.White,
            fontSize = 64.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "$highTemp $lowTemp",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = city,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun WeatherConditionInfo(condition: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Center
    ) {
        WeatherIcon()
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = condition,
            color = Color.White,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun WeatherIcon() {
    Box(
        modifier = Modifier
            .size(80.dp)
            .background(
                color = Purple700,
                shape = RoundedCornerShape(40.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier
                .size(48.dp),
            painter = painterResource(id = R.drawable.ic_weather),
            contentDescription = "Weather Icon",
            colorFilter = ColorFilter.tint(Purple80)
        )
    }
}
