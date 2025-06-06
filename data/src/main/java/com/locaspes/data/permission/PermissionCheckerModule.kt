package com.locaspes.data.permission

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object PermissionCheckerModule {
    @Provides
    fun providePermissionChecker(@ApplicationContext context: Context): PermissionChecker{
        return PermissionCheckerImpl(context)
    }
}