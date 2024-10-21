package com.example.dineout.ui.screens

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
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest

@Composable
fun SearchScreen(navController: NavHostController) {
    val query = remember { mutableStateOf("") }
    val searchResults = remember { mutableStateListOf<Place>() }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextField(
            value = query.value,
            onValueChange = {
                query.value = it
                performSearch(context, it, searchResults)
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Search Restaurants") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(searchResults) { place ->
                RestaurantItem(place)
            }
        }
    }
}

@Composable
fun RestaurantItem(place: Place) {
    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Text(
            text = place.name ?: "Unknown",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = place.address ?: "Address not available",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}


fun performSearch(context: android.content.Context, query: String, searchResults: SnapshotStateList<Place>) {
    if (query.isBlank()) return

    val placesClient = Places.createClient(context)

    val request = FindAutocompletePredictionsRequest.builder()
        .setQuery(query)
        //.setTypeFilter(TypeFilter.ESTABLISHMENT)
        .build()

    placesClient.findAutocompletePredictions(request)
        .addOnSuccessListener { response ->
            searchResults.clear()
            for (prediction in response.autocompletePredictions) {
                val place = Place(
                    name = prediction.getPrimaryText(null).toString(),
                    address = prediction.getSecondaryText(null).toString()
                )
                searchResults.add(place)
            }
        }
        .addOnFailureListener { exception ->
        }
}

data class Place(
    val name: String,
    val address: String
)