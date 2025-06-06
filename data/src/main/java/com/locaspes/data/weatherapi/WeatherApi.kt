package com.locaspes.data.weatherapi

import com.locaspes.data.model.WeatherData
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("data/2.5/weather?")
    suspend fun getCurrentWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        /*
        я знаю что это небезопасно,
        но думаю в рамках тестового
        нет резона мудрить
         */
        @Query("appid") apiKey: String = "63bda7098c746f42b2df2ed334f20665",
        @Query("units") units: String = "metric"
    ): WeatherData

    @GET("data/2.5/forecast")
    suspend fun getForecast(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String = "63bda7098c746f42b2df2ed334f20665",
        @Query("units") units: String = "metric"
    ): WeatherData
}