package com.yei.dev.weather_app.presentation.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.yei.dev.weather_app.presentation.search.WeatherSearchScreen
import com.yei.dev.weather_app.presentation.splash.SplashScreen

@Composable
fun NavWrapper(modifier: Modifier) {
    val backStack = rememberNavBackStack(Routes.Splash)
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Routes.Splash> {
                SplashScreen(
                    onNavigate = {
                        backStack.add(Routes.WeatherSearch)
                        backStack.remove(Routes.Splash)
                    }
                )
            }
            entry<Routes.WeatherSearch> {
                WeatherSearchScreen()
            }

            entry<Routes.Error> {
                Text("Error")
            }
        }
    )
}
