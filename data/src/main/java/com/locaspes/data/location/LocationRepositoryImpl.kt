package com.locaspes.data.location

import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.locaspes.data.permission.PermissionChecker
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val permissionChecker: PermissionChecker,
    private val context: Context
) : LocationRepository {

    override suspend fun getCurrentLocation(): Result<Location> {
        return try {
            if (!permissionChecker.hasLocationPermission()) {
                return Result.failure(LocationPermissionException())
            }

            val location = withContext(Dispatchers.IO) {
                val deferred = CompletableDeferred<Location>()
                fusedLocationProviderClient.lastLocation
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            deferred.complete(location)
                        } else {
                            deferred.completeExceptionally(LocationNotAvailableException())
                        }
                    }
                    .addOnFailureListener { exception ->
                        deferred.completeExceptionally(exception)
                    }
                deferred.await()
            }
            Result.success(location)
        } catch (e: SecurityException) {
            Result.failure(LocationPermissionException(e))
        } catch (e: LocationNotAvailableException) {
            Result.failure(LocationNotAvailableException(e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCityName(latitude: Double, longitude: Double): Result<String> {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            val cityName = addresses?.firstOrNull()?.locality ?: "Неизвестный город"
            Result.success(cityName)
        } catch (e: Exception) {
            Result.failure(Exception("Не удалось найти город!"))
        }
    }
}


class LocationPermissionException(cause: Throwable? = null):
    Exception("что то с разрешением на геолокации", cause)
class LocationNotAvailableException(cause: Throwable? = null)
    : Exception("Что то с геолокацией")