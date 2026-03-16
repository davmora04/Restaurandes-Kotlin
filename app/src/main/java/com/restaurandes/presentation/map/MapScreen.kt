package com.restaurandes.presentation.map

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@SuppressLint("MissingPermission")
@Composable
fun MapScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val fineLocationPermission = rememberPermissionState(
        permission = Manifest.permission.ACCESS_FINE_LOCATION
    )

    val bogota = LatLng(4.7110, -74.0721)
    val cameraPositionState = rememberCameraPositionState()

    var userLatLng by remember { mutableStateOf<LatLng?>(null) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (!fineLocationPermission.status.isGranted) {
            fineLocationPermission.launchPermissionRequest()
        }
    }

    LaunchedEffect(fineLocationPermission.status.isGranted) {
        if (!fineLocationPermission.status.isGranted) {
            isLoading = false
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(bogota, 12f)
            )
            return@LaunchedEffect
        }

        try {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

            val location: Location? = fusedLocationClient
                .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .awaitOrNull()

            if (location != null) {
                val latLng = LatLng(location.latitude, location.longitude)
                userLatLng = latLng
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newLatLngZoom(latLng, 16f)
                )
            } else {
                errorMsg = "No pude obtener tu ubicación. Te muestro Bogotá por defecto."
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newLatLngZoom(bogota, 12f)
                )
            }
        } catch (e: Exception) {
            errorMsg = "Error obteniendo ubicación. Te muestro Bogotá por defecto."
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(bogota, 12f)
            )
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Map View") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = fineLocationPermission.status.isGranted
                ),
                uiSettings = MapUiSettings(
                    myLocationButtonEnabled = fineLocationPermission.status.isGranted,
                    zoomControlsEnabled = true,
                    compassEnabled = true
                )
            ) {
                userLatLng?.let { latLng ->
                    Marker(
                        state = MarkerState(position = latLng),
                        title = "Tu ubicación"
                    )
                }

                // AQUÍ luego van los markers de restaurantes
                // Ejemplo:
                // Marker(
                //     state = MarkerState(position = LatLng(rest.latitude, rest.longitude)),
                //     title = rest.name,
                //     onClick = {
                //         onNavigateToDetail(rest.id)
                //         false
                //     }
                // )
            }

            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                errorMsg != null -> {
                    Text(
                        text = errorMsg!!,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = androidx.compose.ui.unit.dp(16))
                    )
                }

                !fineLocationPermission.status.isGranted -> {
                    Text(
                        text = "Permiso de ubicación no concedido. Te muestro Bogotá por defecto.",
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = androidx.compose.ui.unit.dp(16))
                    )
                }
            }
        }
    }
}

private suspend fun <T> com.google.android.gms.tasks.Task<T>.awaitOrNull(): T? =
    suspendCancellableCoroutine { cont ->
        addOnSuccessListener { cont.resume(it) }
        addOnFailureListener { cont.resume(null) }
        addOnCanceledListener { cont.cancel() }
    }