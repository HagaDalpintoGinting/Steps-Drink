package com.example.stepdrink.ui.screen.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.stepdrink.ui.components.StatCard
import com.example.stepdrink.ui.navigation.Screen
import com.example.stepdrink.viewmodel.StepViewModel
import com.example.stepdrink.viewmodel.WaterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    stepViewModel: StepViewModel,
    waterViewModel: WaterViewModel
) {
    val todaySteps by stepViewModel.todaySteps.collectAsState()
    val stepGoal by stepViewModel.dailyGoal.collectAsState()
    val todayWater by waterViewModel.todayTotalWater.collectAsState()
    val waterGoal by waterViewModel.dailyGoal.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Step & Drink",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Aktivitas Hari Ini",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            // Steps Card
            StatCard(
                icon = Icons.Default.DirectionsWalk,
                title = "Langkah",
                value = "${todaySteps?.steps ?: 0}",
                subtitle = "Target: $stepGoal langkah",
                progress = (todaySteps?.steps ?: 0).toFloat() / stepGoal.toFloat(),
                onClick = { navController.navigate(Screen.Steps.route) }
            )

            // Water Card
            StatCard(
                icon = Icons.Default.WaterDrop,
                title = "Air Minum",
                value = "${todayWater}ml",
                subtitle = "Target: ${waterGoal}ml",
                progress = todayWater.toFloat() / waterGoal.toFloat(),
                onClick = { navController.navigate(Screen.Water.route) }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Tips Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "💡 Tips Kesehatan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Berjalan 10.000 langkah per hari dan minum 2 liter air dapat meningkatkan kesehatan tubuh!",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}