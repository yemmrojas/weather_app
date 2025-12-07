package com.yei.dev.weather_app.presentation.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yei.dev.weather_app.R
import com.yei.dev.weather_app.ui.theme.Purple40
import com.yei.dev.weather_app.ui.theme.Purple700
import com.yei.dev.weather_app.ui.theme.Purple80

@Composable
fun SplashScreen() {
    ContentScreen()
}

@Composable
private fun ContentScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Purple40),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .clip(shape = CircleShape)
                .background(color = Purple700),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                modifier = Modifier
                    .width(80.dp)
                    .height(80.dp)
                    .padding(8.dp),
                painter = painterResource(id = R.drawable.ic_weather),
                contentDescription = "Splash Image",
                colorFilter = ColorFilter.tint(Purple80)
            )
        }
        Text(
            text = "WeatherApp",
            modifier = Modifier.padding(top = 8.dp),
            color = Purple80,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
