package com.example.dineout

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.example.dineout.ui.theme.DineOutTheme
import com.google.android.libraries.places.api.Places

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Places.initialize(applicationContext, "AIzaSyBK39vnO-eFhLR235StXFbgyTNWdQslXfk")

        setContent {
            DineOutTheme {
                val navController = rememberNavController()
                DineOutApp(navController)
            }
        }
    }
}