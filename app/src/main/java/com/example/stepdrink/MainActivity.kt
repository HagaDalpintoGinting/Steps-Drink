package com.example.stepdrink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.stepdrink.ui.navigation.NavGraph
import com.example.stepdrink.ui.theme.StepDrinkTheme
import com.example.stepdrink.viewmodel.StepViewModel
import com.example.stepdrink.viewmodel.WaterViewModel
import com.example.stepdrink.viewmodel.ProfileViewModel
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StepDrinkTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val stepViewModel: StepViewModel = viewModel()
                    val waterViewModel: WaterViewModel = viewModel()
                    val profileViewModel: ProfileViewModel = viewModel()

                    NavGraph(
                        navController = navController,
                        stepViewModel = stepViewModel,
                        waterViewModel = waterViewModel,
                        profileViewModel = profileViewModel
                    )
                }
            }
        }
    }
}