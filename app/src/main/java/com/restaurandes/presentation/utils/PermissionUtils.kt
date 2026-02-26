package com.restaurandes.presentation.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/**
 * Utility functions for handling runtime permissions
 * Sprint 2 - Location Sensor Implementation
 */

/**
 * Check if location permissions are granted
 */
fun Context.hasLocationPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED ||
    ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

/**
 * Composable function to request location permissions
 * 
 * @param onPermissionGranted Callback when permission is granted
 * @param onPermissionDenied Callback when permission is denied
 * @return Lambda to trigger permission request
 */
@Composable
fun rememberLocationPermissionState(
    onPermissionGranted: () -> Unit = {},
    onPermissionDenied: () -> Unit = {}
): LocationPermissionState {
    val context = LocalContext.current
    
    var hasPermission by remember {
        mutableStateOf(context.hasLocationPermission())
    }
    
    var shouldShowRationale by remember { mutableStateOf(false) }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.any { it }
        hasPermission = granted
        
        if (granted) {
            onPermissionGranted()
        } else {
            shouldShowRationale = true
            onPermissionDenied()
        }
    }
    
    return LocationPermissionState(
        hasPermission = hasPermission,
        shouldShowRationale = shouldShowRationale,
        requestPermission = {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    )
}

/**
 * State holder for location permissions
 */
data class LocationPermissionState(
    val hasPermission: Boolean,
    val shouldShowRationale: Boolean,
    val requestPermission: () -> Unit
)
