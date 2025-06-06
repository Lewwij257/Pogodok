package com.locaspes.presentation.weatherSection.usecase

import com.locaspes.data.weather.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object WeatherSectionUseCaseModule {
    @Provides
    fun provideWeatherSectionUseCase(weatherRepository: WeatherRepository): WeatherSectionUseCase {
        return WeatherSectionUseCase(weatherRepository)
    }
}