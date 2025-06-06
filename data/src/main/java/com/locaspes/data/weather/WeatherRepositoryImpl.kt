package com.locaspes.data.weather

import android.util.Log
import com.locaspes.data.location.LocationNotAvailableException
import com.locaspes.data.location.LocationPermissionException
import com.locaspes.data.location.LocationRepository
import com.locaspes.data.model.DailyForecast
import com.locaspes.data.model.ForecastItem
import com.locaspes.data.model.Temperature
import com.locaspes.data.model.WeatherData
import com.locaspes.data.weatherapi.WeatherApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val locationRepository: LocationRepository,
    private val api: WeatherApi
) : WeatherRepository {

    override val weatherFlow: Flow<Result<WeatherData>> = flow {
        while (true) {
            val locationResult = locationRepository.getCurrentLocation()

            when {
                locationResult.isSuccess -> {
                    try {
                        val location = locationResult.getOrThrow()

                        val currentWeather = api.getCurrentWeather(
                            latitude = location.latitude,
                            longitude = location.longitude,
                        )
                        Log.d("weatherMain", currentWeather.currentWeatherData?.weatherCondition?.get(0).toString())

                        val forecast = api.getForecast(
                            latitude = location.latitude,
                            longitude = location.longitude,
                        )

                        val dailyForecast = aggregateDailyForecast(forecast.forecastList)

                        val combinedWeather = WeatherData(
                            currentWeatherData = currentWeather.currentWeatherData,
                            forecastList = forecast.forecastList,
                            daily = dailyForecast,
                            name = currentWeather.name
                        )
                        emit(Result.success(combinedWeather))
                    } catch (e: Exception) {
                        emit(Result.failure(e))
                    }
                }
                locationResult.isFailure -> {
                    val exception = locationResult.exceptionOrNull()
                    when (exception) {
                        is LocationPermissionException -> emit(Result.failure(LocationPermissionException()))
                        is LocationNotAvailableException -> emit(Result.failure(LocationNotAvailableException()))
                        else -> emit(Result.failure(Exception("Не удалось получить местоположение")))
                    }
                }
            }
            delay(60_000)
        }
    }.shareIn(
        scope = CoroutineScope(Dispatchers.IO),
        started = SharingStarted.WhileSubscribed(),
        replay = 1
    )

    private fun aggregateDailyForecast(forecastList: List<ForecastItem>?): List<DailyForecast> {
        if (forecastList.isNullOrEmpty()) return emptyList()

        val dailyMap = mutableMapOf<String, MutableList<ForecastItem>>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        forecastList.forEach { item ->
            val date = dateFormat.format(Date(item.dt * 1000))
            dailyMap.getOrPut(date) { mutableListOf() }.add(item)
        }

        return dailyMap.map { (date, items) ->
            val minTemp = items.minOfOrNull { it.main.temperature }?.toFloat() ?: 0f
            val maxTemp = items.maxOfOrNull { it.main.temperature }?.toFloat() ?: 0f
            val firstWeather = items.firstOrNull()?.weatherCondition?.firstOrNull()
            DailyForecast(
                dt = items.first().dt,
                temp = Temperature(min = minTemp, max = maxTemp),
                weatherCondition = firstWeather?.let { listOf(it) } ?: emptyList()
            )
        }
    }
}