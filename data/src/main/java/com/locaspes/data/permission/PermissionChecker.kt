package com.locaspes.data.permission

interface PermissionChecker {
    fun hasLocationPermission(): Boolean
    fun hasInternetPermission(): Boolean
}