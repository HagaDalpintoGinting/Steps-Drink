package com.example.stepdrink.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.stepdrink.ui.screen.home.HomeScreen
import com.example.stepdrink.ui.screen.steps.StepsScreen
import com.example.stepdrink.ui.screen.water.WaterScreen
import com.example.stepdrink.viewmodel.StepViewModel
import com.example.stepdrink.viewmodel.WaterViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    stepViewModel: StepViewModel,
    waterViewModel: WaterViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                navController = navController,
                stepViewModel = stepViewModel,
                waterViewModel = waterViewModel
            )
        }

        composable(Screen.Steps.route) {
            StepsScreen(
                navController = navController,
                viewModel = stepViewModel
            )
        }

        composable(Screen.Water.route) {
            WaterScreen(
                navController = navController,
                viewModel = waterViewModel
            )
        }
    }
}