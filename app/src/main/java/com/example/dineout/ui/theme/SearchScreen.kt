package com.example.dineout.ui.screens

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.gms.maps.model.LatLng
import androidx.compose.runtime.remember
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.libraries.places.api.model.Place.Field
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.net.FetchPlaceRequest

@Composable
fun SearchScreen(navController: NavHostController) {
    val query = remember { mutableStateOf("") }
    val searchResults = remember { mutableStateListOf<Place>() }
    val context = LocalContext.current

    val initialPosition = LatLng(27.9506, -82.4572)
    val cameraPosition = remember { CameraPosition.builder().target(initialPosition).zoom(10f).build() }

    var map: GoogleMap? by remember { mutableStateOf(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    )
    {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            AndroidView(
                factory = { context ->
                    MapsInitializer.initialize(context)
                    MapView(context).apply {
                        onCreate(null)
                        getMapAsync { googleMap ->
                            map = googleMap
                            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                            updateMapMarkers(googleMap, searchResults)
                        }
                    }
                },
                update = {
                    map?.let {
                        updateMapMarkers(it, searchResults)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = query.value,
            onValueChange = {
                query.value = it
                performSearch(context, it, searchResults)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            label = { Text("Search Restaurants") },
            singleLine = true
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(searchResults) { place ->
                RestaurantItem(place) {
                    moveToLocation(place.latLng, map)
                }
            }
        }
    }
}

private fun updateMapMarkers(googleMap: GoogleMap, searchResults: List<Place>) {
    googleMap.clear()
    searchResults.forEach { place ->
        googleMap.addMarker(
            MarkerOptions()
                .position(place.latLng ?: LatLng(0.0, 0.0))
                .title(place.name)
                .snippet(place.address)
        )
    }
}

@Composable
fun RestaurantItem(place: Place, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Text(
            text = place.name,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = place.address,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

fun moveToLocation(latLng: LatLng?, map: GoogleMap?) {
    latLng?.let {
        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 15f))
    }
}

fun performSearch(context: Context, query: String, searchResults: SnapshotStateList<Place>) {
    if (query.isBlank()) return

    val placesClient = Places.createClient(context)

    val request = FindAutocompletePredictionsRequest.builder()
        .setQuery(query)
        .build()

    placesClient.findAutocompletePredictions(request)
        .addOnSuccessListener { response ->
            searchResults.clear()
            for (prediction in response.autocompletePredictions) {
                val placeId = prediction.placeId
                val placeName = prediction.getPrimaryText(null).toString()
                val placeAddress = prediction.getSecondaryText(null).toString()

                fetchPlaceDetails(context, placeId) { latLng ->
                    val place = Place(
                        name = placeName,
                        address = placeAddress,
                        latLng = latLng,
                        placeId = placeId
                    )
                    searchResults.add(place)
                }
            }
        }
        .addOnFailureListener { exception ->
        }
}

fun fetchPlaceDetails(context: Context, placeId: String, callback: (LatLng?) -> Unit) {
    val placesClient = Places.createClient(context)

    val request = FetchPlaceRequest.builder(placeId, listOf(Field.LAT_LNG)).build()

    placesClient.fetchPlace(request)
        .addOnSuccessListener { response ->
            val place = response.place
            callback(place.latLng)
        }
        .addOnFailureListener { exception ->
            callback(null)
        }
}

data class Place(
    val name: String,
    val address: String,
    val latLng: LatLng? = null,
    val placeId: String
)