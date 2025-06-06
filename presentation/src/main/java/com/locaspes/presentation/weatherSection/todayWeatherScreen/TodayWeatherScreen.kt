package com.locaspes.presentation.weatherSection.todayWeatherScreen

import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.locaspes.presentation.weatherSection.viewmodel.WeatherSectionUiState
import com.locaspes.presentation.weatherSection.viewmodel.WeatherSectionViewModel




@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Composable
fun TodayWeatherScreen(
    viewModel: WeatherSectionViewModel,
    onNavigateToWeekWeatherScreen: () -> Unit
) {

    var hasLocationPermission by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted
    }

    val locationSettingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
    }

    val weatherState by viewModel.weatherDataFlow.collectAsState()

    val backgroundResId = when (weatherState) {
        is WeatherSectionUiState.Success -> {
            val weatherMain = (weatherState as WeatherSectionUiState.Success).data.currentWeatherData?.weatherCondition?.firstOrNull()?.main
            Log.d("weatherMain", weatherMain?:":(")
            when (weatherMain) {
                "Clear" -> com.locaspes.theme.R.drawable.sunny
                "Clouds" -> com.locaspes.theme.R.drawable.cloudy
                "Rain" -> com.locaspes.theme.R.drawable.rainy
                "Snow" -> com.locaspes.theme.R.drawable.snowy
                "Thunderstorm" -> com.locaspes.theme.R.drawable.thunderstorm
                else -> com.locaspes.theme.R.drawable.sunny
            }
        }
        else -> com.locaspes.theme.R.drawable.sunny
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f))
    ) {
        Image(
            painter = painterResource(id = backgroundResId),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            when (weatherState) {
                is WeatherSectionUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                is WeatherSectionUiState.Success -> {
                    val weatherData = (weatherState as WeatherSectionUiState.Success).data
                    val current = weatherData.currentWeatherData

                    Text(
                        text = weatherData.name,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 32.sp
                        ),
                        modifier = Modifier.padding(top = 24.dp)
                    )

                    Text(
                        text = current?.weatherCondition?.firstOrNull()?.description?.replaceFirstChar { it.uppercase() } ?: "Нет данных",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 20.sp
                        ),
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "${current?.temperature?.toInt()}°C",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 64.sp
                        )
                    )

                    Text(
                        text = "Влажность: ${current?.humidity ?: 60}%",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 18.sp
                        ),
                        modifier = Modifier.padding(top = 16.dp)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = {
                            onNavigateToWeekWeatherScreen()
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.2f),
                            contentColor = Color.White
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            text = "Прогноз на неделю",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp
                            )
                        )
                    }
                }
                is WeatherSectionUiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Ошибка: ${(weatherState as WeatherSectionUiState.Error).message}",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                when {
                                    (weatherState as WeatherSectionUiState.Error).message.contains("permission") -> {
                                        permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                                    }
                                    (weatherState as WeatherSectionUiState.Error).message.contains("location") -> {
                                        locationSettingsLauncher.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                                    }
                                    else -> {
                                        viewModel.refreshWeatherData()
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White.copy(alpha = 0.2f),
                                contentColor = Color.White
                            )
                        ) {
                            Text("Повторить")
                        }
                    }
                }
            }
        }
    }
}