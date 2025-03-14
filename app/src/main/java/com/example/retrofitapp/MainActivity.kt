package com.example.retrofitapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.retrofitapp.navigation.AppNavigation
import com.example.retrofitapp.ui.theme.RetrofitAppTheme
import com.example.retrofitapp.viewmodel.APIViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: APIViewModel by viewModels()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RetrofitAppTheme {
                val windowSizeClass = calculateWindowSizeClass(this)
                val navController = rememberNavController()
                
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        navController = navController,
                        viewModel = viewModel,
                        windowSizeClass = windowSizeClass.widthSizeClass
                    )
                }
            }
        }
    }
}