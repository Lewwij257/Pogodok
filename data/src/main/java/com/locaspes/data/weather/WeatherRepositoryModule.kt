package com.locaspes.data.weather

import com.locaspes.data.location.LocationRepository
import com.locaspes.data.weatherapi.WeatherApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WeatherRepositoryModule {
    @Provides
    @Singleton
    fun provideWeatherRepository(
        locationRepository: LocationRepository,
        weatherApi: WeatherApi
    ): WeatherRepository{
        return WeatherRepositoryImpl(
            locationRepository,
            weatherApi
        )
    }
}