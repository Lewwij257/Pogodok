package com.locaspes.pogodok

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.locaspes.pogodok.ui.theme.PogodokTheme
import com.locaspes.presentation.weatherSection.todayWeatherScreen.TodayWeatherScreen
import com.locaspes.presentation.weatherSection.viewmodel.WeatherSectionViewModel
import com.locaspes.presentation.weatherSection.weekWeatherScreen.WeekWeatherScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PogodokTheme {
                AppNavHost()
            }
        }
    }
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.TodayWeatherScreen.route) {
        composable(Screen.TodayWeatherScreen.route) {
            val viewModel: WeatherSectionViewModel = hiltViewModel()
            TodayWeatherScreen(
                viewModel = viewModel,
                onNavigateToWeekWeatherScreen = {navController.navigate(Screen.WeekWeatherScreen.route)}
            )
        }
        composable(Screen.WeekWeatherScreen.route) {
            val viewModel: WeatherSectionViewModel = hiltViewModel()
            WeekWeatherScreen(
                viewModel = viewModel,
                onNavigateBack = {navController.navigate(Screen.TodayWeatherScreen.route)}
            )
        }
    }
}