package com.example.dineout

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.dineout.ui.screens.HomeScreen
import com.example.dineout.ui.screens.FavoritesScreen

@Composable
fun DineOutApp(navController: NavHostController) {
    NavHost(navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("favorites") { FavoritesScreen(navController) }
    }
}