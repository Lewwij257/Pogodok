package com.locaspes.data.model

import com.google.gson.annotations.SerializedName

data class WeatherData(
    @SerializedName("main")
    val currentWeatherData: CurrentWeather? = null, // Для /weather
    @SerializedName("list")
    val forecastList: List<ForecastItem>? = null, // Для /forecast
    @SerializedName("daily")
    val daily: List<DailyForecast>? = emptyList(),
    val name: String
)

data class CurrentWeather(
    @SerializedName("temp")
    val temperature: Double,
    @SerializedName("humidity")
    val humidity: Int?,
    @SerializedName("weather")
    val weatherCondition: List<WeatherCondition>?
)

data class ForecastItem(
    @SerializedName("dt")
    val dt: Long,
    @SerializedName("main")
    val main: MainForecast,
    @SerializedName("weather")
    val weatherCondition: List<WeatherCondition>?
)

data class MainForecast(
    @SerializedName("temp")
    val temperature: Double,
    @SerializedName("humidity")
    val humidity: Int?
)

data class DailyForecast(
    @SerializedName("dt")
    val dt: Long,
    @SerializedName("temp")
    val temp: Temperature,
    @SerializedName("weather")
    val weatherCondition: List<WeatherCondition>
)

data class Temperature(
    @SerializedName("min")
    val min: Float,
    @SerializedName("max")
    val max: Float
)

data class WeatherCondition(
    @SerializedName("main")
    val main: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("icon")
    val icon: String
)