package com.example.stepdrink.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.stepdrink.ui.navigation.Screen
import com.example.stepdrink.viewmodel.StepViewModel
import com.example.stepdrink.viewmodel.WaterViewModel
import com.example.stepdrink.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    stepViewModel: StepViewModel,
    waterViewModel: WaterViewModel,
    profileViewModel: ProfileViewModel
) {
    val todaySteps by stepViewModel.todaySteps.collectAsState()
    val stepGoal by stepViewModel.dailyGoal.collectAsState()
    val todayWater by waterViewModel.todayTotalWater.collectAsState()
    val waterGoal by waterViewModel.dailyGoal.collectAsState()
    val userName by profileViewModel.userName.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Step",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "&",
                            fontWeight = FontWeight.Light,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Drink",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { navController.navigate(Screen.Profile.route) }
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Person,
                                    "Profil",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
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
            // Greeting Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    MaterialTheme.colorScheme.tertiaryContainer
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            text = "Halo, $userName! 👋",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Tetap sehat dengan tracking harian",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Text(
                text = "Aktivitas Hari Ini",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // Steps Card - Improved
            ImprovedStatCard(
                icon = Icons.Default.DirectionsWalk,
                title = "Langkah",
                value = "${todaySteps?.steps ?: 0}",
                unit = "langkah",
                subtitle = "Target: $stepGoal langkah",
                progress = (todaySteps?.steps ?: 0).toFloat() / stepGoal.toFloat(),
                gradientColors = listOf(
                    Color(0xFF4CAF50),
                    Color(0xFF81C784)
                ),
                onClick = { navController.navigate(Screen.Steps.route) }
            )

            // Water Card - Improved
            ImprovedStatCard(
                icon = Icons.Default.WaterDrop,
                title = "Air Minum",
                value = "$todayWater",
                unit = "ml",
                subtitle = "Target: ${waterGoal}ml",
                progress = todayWater.toFloat() / waterGoal.toFloat(),
                gradientColors = listOf(
                    Color(0xFF2196F3),
                    Color(0xFF64B5F6)
                ),
                onClick = { navController.navigate(Screen.Water.route) }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Tips Card - Improved
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "💡",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Column {
                        Text(
                            text = "Tips Kesehatan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Berjalan 10.000 langkah per hari dan minum 2 liter air dapat meningkatkan kesehatan tubuh!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ImprovedStatCard(
    icon: ImageVector,
    title: String,
    value: String,
    unit: String,
    subtitle: String,
    progress: Float,
    gradientColors: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(gradientColors)
                )
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = title,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White.copy(alpha = 0.9f),
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = value,
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = unit,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    }

                    // Progress Circle
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(60.dp)
                    ) {
                        CircularProgressIndicator(
                            progress = { progress.coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxSize(),
                            color = Color.White,
                            strokeWidth = 6.dp,
                            trackColor = Color.White.copy(alpha = 0.3f)
                        )
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LinearProgressIndicator(
                    progress = { progress.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(MaterialTheme.shapes.small),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.3f),
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}