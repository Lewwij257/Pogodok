package com.locaspes.data.location

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.locaspes.data.permission.PermissionChecker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object LocationRepositoryModule {
    @Provides
    fun provideLocationRepository(
        fusedLocationProviderClient: FusedLocationProviderClient,
        permissionChecker: PermissionChecker,
        @ApplicationContext context: Context
    ): LocationRepository{
        return LocationRepositoryImpl(fusedLocationProviderClient, permissionChecker, context)
    }
}