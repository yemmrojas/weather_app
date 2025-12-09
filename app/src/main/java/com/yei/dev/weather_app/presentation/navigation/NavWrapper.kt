package com.yei.dev.weather_app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.yei.dev.weather_app.presentation.detail.WeatherDetailScreen
import com.yei.dev.weather_app.presentation.search.WeatherSearchScreen

@Composable
fun NavWrapper(modifier: Modifier) {
    val backStack = rememberNavBackStack(Routes.WeatherSearch)
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Routes.WeatherSearch> {
                WeatherSearchScreen(
                    onBackClick = {
                        backStack.removeLastOrNull()
                    },
                    onLocationSelected = { location ->
                        backStack.add(Routes.WeatherDetail(location = location.name))
                    }
                )
            }

            entry<Routes.WeatherDetail> { route ->
                WeatherDetailScreen(
                    location = route.location,
                    onBackClick = {
                        backStack.removeLastOrNull()
                    }
                )
            }
        }
    )
}
