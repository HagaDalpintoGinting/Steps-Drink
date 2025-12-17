package com.example.stepdrink.ui.screen.steps

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.stepdrink.viewmodel.StepViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepsScreen(
    navController: NavController,
    viewModel: StepViewModel
) {
    val todaySteps by viewModel.todaySteps.collectAsState()
    val stepGoal by viewModel.dailyGoal.collectAsState()
    val sensorSteps by viewModel.stepCounterManager.totalSteps.collectAsState()
    val last7Days by viewModel.last7DaysSteps.collectAsState()
    val userWeight by viewModel.userWeight.collectAsState()  // TAMBAH INI

    var isTracking by remember { mutableStateOf(false) }
    var permissionGranted by remember { mutableStateOf(false) }

    // Calculate calories - TAMBAH INI
    val todayCalories = remember(todaySteps?.steps, userWeight) {
        viewModel.calculateCalories(todaySteps?.steps ?: 0, userWeight)
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionGranted = isGranted
        if (isGranted) {
            viewModel.stepCounterManager.startTracking()
            isTracking = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pelacak Langkah") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    if (isTracking) {
                        viewModel.stepCounterManager.stopTracking()
                        isTracking = false
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                        } else {
                            viewModel.stepCounterManager.startTracking()
                            isTracking = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (isTracking) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = if (isTracking) "Stop" else "Start"
                    )
                },
                text = { Text(if (isTracking) "Berhenti" else "Mulai Tracking") },
                containerColor = if (isTracking) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Main Steps Card dengan Gradient
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF4CAF50),
                                        Color(0xFF81C784)
                                    )
                                )
                            )
                            .padding(24.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Animated Icon
                            if (isTracking) {
                                AnimatedWalkingIcon()
                            } else {
                                Icon(
                                    imageVector = Icons.Default.DirectionsWalk,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "${todaySteps?.steps ?: 0}",
                                style = MaterialTheme.typography.displayLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Text(
                                text = "Langkah Hari Ini",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White.copy(alpha = 0.9f)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            LinearProgressIndicator(
                                progress = {
                                    (todaySteps?.steps ?: 0).toFloat() / stepGoal.toFloat()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(MaterialTheme.shapes.small),
                                color = Color.White,
                                trackColor = Color.White.copy(alpha = 0.3f),
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Target: $stepGoal langkah",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f)
                            )

                            if (isTracking) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Surface(
                                    shape = MaterialTheme.shapes.small,
                                    color = Color.White.copy(alpha = 0.2f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        PulsingDot()
                                        Text(
                                            "Sedang Tracking: $sensorSteps langkah",
                                            color = Color.White,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // TAMBAH INI - Calories Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFFFF6B6B),
                                        Color(0xFFFF8E53)
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "🔥",
                                        style = MaterialTheme.typography.headlineMedium
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Kalori Terbakar",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                    Text(
                                        text = "$todayCalories kkal",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                            Column(
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = "Berat: ${userWeight}kg",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = "≈ ${String.format("%.2f", todayCalories / 7.7)} gram lemak",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }

            // Motivational Message - TAMBAH INI
            item {
                val message = when {
                    (todaySteps?.steps ?: 0) >= stepGoal -> "🎉 Luar biasa! Target tercapai!"
                    (todaySteps?.steps ?: 0) >= (stepGoal * 0.75) -> "💪 Hampir sampai! Tetap semangat!"
                    (todaySteps?.steps ?: 0) >= (stepGoal * 0.5) -> "🚶 Setengah jalan! Ayo lanjutkan!"
                    (todaySteps?.steps ?: 0) >= (stepGoal * 0.25) -> "👟 Awal yang bagus! Terus bergerak!"
                    else -> "🌟 Yuk mulai bergerak hari ini!"
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Riwayat 7 Hari Terakhir",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            items(last7Days) { record ->
                val recordCalories = viewModel.calculateCalories(record.steps, userWeight)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = record.date,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${record.steps} langkah • $recordCalories kkal",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            CircularProgressIndicator(
                                progress = { (record.steps.toFloat() / stepGoal.toFloat()).coerceIn(0f, 1f) },
                                modifier = Modifier.size(48.dp),
                                strokeWidth = 4.dp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${((record.steps.toFloat() / stepGoal.toFloat()) * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedWalkingIcon() {
    val infiniteTransition = rememberInfiniteTransition(label = "walking")
    val rotation by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )

    Icon(
        imageVector = Icons.Default.DirectionsWalk,
        contentDescription = null,
        modifier = Modifier
            .size(64.dp)
            .rotate(rotation),
        tint = Color.White
    )
}

@Composable
fun PulsingDot() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = alpha))
    )
}