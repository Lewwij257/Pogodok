package com.locaspes.presentation.weatherSection.usecase

import com.locaspes.data.model.WeatherData
import com.locaspes.data.weather.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WeatherSectionUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
){
    val weatherDataFlow: Flow<Result<WeatherData>> = weatherRepository.weatherFlow
}