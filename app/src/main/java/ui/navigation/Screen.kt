package com.example.stepdrink.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Steps : Screen("steps")
    object Water : Screen("water")
}