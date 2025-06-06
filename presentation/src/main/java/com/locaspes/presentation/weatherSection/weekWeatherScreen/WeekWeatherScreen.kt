package com.locaspes.presentation.weatherSection.weekWeatherScreen

import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.locaspes.data.model.DailyForecast
import com.locaspes.presentation.weatherSection.viewmodel.WeatherSectionUiState
import com.locaspes.presentation.weatherSection.viewmodel.WeatherSectionViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Composable
fun WeekWeatherScreen(
    viewModel: WeatherSectionViewModel,
    onNavigateBack: () -> Unit
) {
    val weatherState by viewModel.weatherDataFlow.collectAsState()

    val backgroundResId = when (weatherState) {
        is WeatherSectionUiState.Success -> {
            val weatherMain = (weatherState as WeatherSectionUiState.Success).data.currentWeatherData?.weatherCondition?.firstOrNull()?.main
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
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (weatherState) {
                is WeatherSectionUiState.Success -> {
                    val weatherData = (weatherState as WeatherSectionUiState.Success).data
                    Text(
                        text = weatherData.name,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 28.sp
                        ),
                        modifier = Modifier
                            .padding(bottom = 16.dp, top = 34.dp)
                    )
                }
                else -> Spacer(modifier = Modifier.height(16.dp))
            }

            when (weatherState) {
                is WeatherSectionUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                is WeatherSectionUiState.Success -> {
                    val weatherData = (weatherState as WeatherSectionUiState.Success).data
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        weatherData.daily?.let {
                            items(it.take(7)) { forecast ->
                                AnimatedVisibility(
                                    visible = true,
                                    enter = fadeIn()
                                ) {
                                    WeatherDayCard(forecast = forecast)
                                }
                            }
                        }
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
                            onClick = { /* TODO: Повторный запрос */ },
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


@Composable
fun WeatherDayCard(forecast: DailyForecast) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = getWeatherIconResId(forecast.weatherCondition.firstOrNull()?.icon ?: "")),
                contentDescription = "Weather icon",
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = SimpleDateFormat("EEEE, dd MMM", Locale.getDefault())
                        .format(Date(forecast.dt * 1000)),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                )
                Text(
                    text = forecast.weatherCondition.firstOrNull()?.description?.replaceFirstChar { it.uppercase() } ?: "",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.8f)
                    )
                )
            }
            Text(
                text = "${forecast.temp.min.toInt()}° / ${forecast.temp.max.toInt()}°C",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
fun getWeatherIconResId(iconCode: String): Int {
    return when (iconCode) {
        "01d" -> com.locaspes.theme.R.drawable.d1
        "01n" -> com.locaspes.theme.R.drawable.n1

        "02d" -> com.locaspes.theme.R.drawable.d2
        "02n" -> com.locaspes.theme.R.drawable.n2

        "04d" -> com.locaspes.theme.R.drawable.d4
        "04n" -> com.locaspes.theme.R.drawable.n4

        "09d" -> com.locaspes.theme.R.drawable.d9
        "09n" -> com.locaspes.theme.R.drawable.n9

        "10d" -> com.locaspes.theme.R.drawable.d10
        "10n" -> com.locaspes.theme.R.drawable.n10

        "11d" -> com.locaspes.theme.R.drawable.d11
        "11n" -> com.locaspes.theme.R.drawable.n11

        "13d" -> com.locaspes.theme.R.drawable.d13
        "13n" -> com.locaspes.theme.R.drawable.n13

        "50d" -> com.locaspes.theme.R.drawable.d50
        "50n" -> com.locaspes.theme.R.drawable.n50

        else -> com.locaspes.theme.R.drawable.d1
    }
}