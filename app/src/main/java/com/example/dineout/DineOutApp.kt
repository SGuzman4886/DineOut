package com.example.dineout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.dineout.ui.screens.HomeScreen
import com.example.dineout.ui.screens.FavoritesScreen
import com.example.dineout.ui.screens.SearchScreen

@Composable
fun DineOutApp(navController: NavHostController) {
    val favoriteRestaurants = remember { mutableStateListOf<String>() }

    NavHost(navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("favorites") { FavoritesScreen(navController, favoriteRestaurants) }
        composable("search") { SearchScreen(navController, favoriteRestaurants) }
    }
}