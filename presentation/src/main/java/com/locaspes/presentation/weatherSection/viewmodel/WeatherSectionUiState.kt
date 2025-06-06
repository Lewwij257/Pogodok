package com.locaspes.presentation.weatherSection.viewmodel

import com.locaspes.data.model.WeatherData

sealed class WeatherSectionUiState {
    data object Loading : WeatherSectionUiState()
    data class Success(val data: WeatherData) : WeatherSectionUiState()
    data class Error(val message: String) : WeatherSectionUiState()
}