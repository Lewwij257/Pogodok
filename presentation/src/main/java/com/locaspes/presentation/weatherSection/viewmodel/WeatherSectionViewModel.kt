package com.locaspes.presentation.weatherSection.viewmodel

import android.net.http.HttpException
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresExtension
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.locaspes.data.location.LocationNotAvailableException
import com.locaspes.data.location.LocationPermissionException
import com.locaspes.presentation.weatherSection.usecase.WeatherSectionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class WeatherSectionViewModel @Inject constructor(
    private val weatherSectionUseCase: WeatherSectionUseCase
) : ViewModel() {

    private val _refreshTrigger = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    val weatherDataFlow = _refreshTrigger.flatMapLatest {
        weatherSectionUseCase.weatherDataFlow
            .map { result ->
                when {
                    result.isSuccess -> {
                        val weatherData = result.getOrNull()
                        if (weatherData != null) {
                            WeatherSectionUiState.Success(weatherData)
                        } else {
                            WeatherSectionUiState.Error("Ошибка загрузки")
                        }
                    }
                    result.isFailure -> {
                        val exception = result.exceptionOrNull()
                        when (exception) {
                            is LocationPermissionException -> WeatherSectionUiState.Error("Пожалуйста, предоставьте разрешение на геолокацию")
                            is LocationNotAvailableException -> WeatherSectionUiState.Error("Местоположение недоступно. Включите геолокацию")
                            else -> WeatherSectionUiState.Error("Неизвестная ошибка: ${exception?.message ?: "Попробуйте позже"}")
                        }
                    }
                    else -> {
                        WeatherSectionUiState.Loading
                    }
                }
            }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = WeatherSectionUiState.Loading
        )

    fun refreshWeatherData() {
        _refreshTrigger.value++
    }
}