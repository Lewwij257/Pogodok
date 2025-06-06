package com.locaspes.data.location

import android.location.Location

interface LocationRepository {
    suspend fun getCurrentLocation(): Result<Location>

    suspend fun getCityName(latitude: Double, longitude: Double): Result<String>
}