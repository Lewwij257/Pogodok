package com.locaspes.data.weather

import com.locaspes.data.model.WeatherData
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    val weatherFlow: Flow<Result<WeatherData>>
}