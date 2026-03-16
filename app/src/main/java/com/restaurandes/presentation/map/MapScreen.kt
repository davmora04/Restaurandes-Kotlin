package com.restaurandes.presentation.map

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@SuppressLint("MissingPermission")
@Composable
fun MapScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: MapViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val fineLocationPermission = rememberPermissionState(
        permission = Manifest.permission.ACCESS_FINE_LOCATION
    )

    val bogota = LatLng(4.7110, -74.0721)
    val cameraPositionState = rememberCameraPositionState()
    val scope = rememberCoroutineScope()

    var userLatLng by remember { mutableStateOf<LatLng?>(null) }
    var locationMessage by remember { mutableStateOf<String?>(null) }
    var isLocatingUser by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (!fineLocationPermission.status.isGranted) {
            fineLocationPermission.launchPermissionRequest()
        }
    }

    LaunchedEffect(fineLocationPermission.status.isGranted) {
        if (!fineLocationPermission.status.isGranted) {
            isLocatingUser = false
            locationMessage = "Permiso de ubicación no concedido. Te muestro Bogotá por defecto."
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(bogota, 12f)
            )
            return@LaunchedEffect
        }

        try {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            val location: Location? =
                fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).await()

            if (location != null) {
                val latLng = LatLng(location.latitude, location.longitude)
                userLatLng = latLng
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newLatLngZoom(latLng, 15f)
                )
            } else {
                locationMessage = "No pude obtener tu ubicación. Te muestro Bogotá por defecto."
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newLatLngZoom(bogota, 12f)
                )
            }
        } catch (_: Exception) {
            locationMessage = "Error obteniendo ubicación. Te muestro Bogotá por defecto."
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(bogota, 12f)
            )
        } finally {
            isLocatingUser = false
        }
    }

    LaunchedEffect(uiState.restaurants) {
        if (userLatLng == null && uiState.restaurants.isNotEmpty() && !isLocatingUser) {
            val firstRestaurant = uiState.restaurants.first()
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(
                    LatLng(firstRestaurant.latitude, firstRestaurant.longitude),
                    13f
                )
            )
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
                    compassEnabled = true,
                    mapToolbarEnabled = true
                )
            ) {
                uiState.restaurants.forEach { restaurant ->
                    if (restaurant.latitude != 0.0 || restaurant.longitude != 0.0) {
                        Marker(
                            state = MarkerState(
                                position = LatLng(restaurant.latitude, restaurant.longitude)
                            ),
                            title = restaurant.name,
                            snippet = "${restaurant.category} • ${restaurant.rating}",
                            onClick = {
                                onNavigateToDetail(restaurant.id)
                                false
                            }
                        )
                    }
                }

                userLatLng?.let { latLng ->
                    Marker(
                        state = MarkerState(position = latLng),
                        title = "Tu ubicación"
                    )
                }
            }

            when {
                uiState.isLoading || isLocatingUser -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.error != null -> {
                    InfoBanner(
                        text = uiState.error ?: "",
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 16.dp)
                    )
                }

                locationMessage != null -> {
                    InfoBanner(
                        text = locationMessage ?: "",
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 16.dp)
                    )
                }
            }

            if (uiState.restaurants.isNotEmpty()) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .widthIn(max = 420.dp),
                    shape = RoundedCornerShape(20.dp),
                    tonalElevation = 6.dp,
                    shadowElevation = 6.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "${uiState.restaurants.size} restaurantes en el mapa",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Toca un marcador para abrir el detalle del restaurante.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoBanner(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                shape = RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}