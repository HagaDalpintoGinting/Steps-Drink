package com.example.stepdrink.ui.screen.steps

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current

    val todaySteps by viewModel.todaySteps.collectAsState()
    val stepGoal by viewModel.dailyGoal.collectAsState()
    val sensorSteps by viewModel.improvedStepManager.totalSteps.collectAsState()
    val last7Days by viewModel.last7DaysSteps.collectAsState()
    val userWeight by viewModel.userWeight.collectAsState()
    val sensorStatus by viewModel.improvedStepManager.sensorStatus.collectAsState()

    // Real-time detection states
    val stepDetected by viewModel.improvedStepManager.stepDetected.collectAsState()
    val isWalking by viewModel.improvedStepManager.isWalking.collectAsState()

    var isTracking by remember { mutableStateOf(false) }
    var permissionGranted by remember { mutableStateOf(false) }
    var showDebugInfo by remember { mutableStateOf(false) }

    // Calculate calories
    val todayCalories = remember(todaySteps?.steps, userWeight) {
        viewModel.calculateCalories(todaySteps?.steps ?: 0, userWeight)
    }

    // Log when sensor steps change
    LaunchedEffect(sensorSteps) {
        Log.d("StepsScreen", "Sensor steps: $sensorSteps")
    }

    // Haptic feedback
    LaunchedEffect(stepDetected) {
        if (stepDetected && isTracking) {
            try {
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(
                        VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE)
                    )
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(30)
                }
            } catch (e: Exception) {
                Log.e("StepsScreen", "Vibration error: ${e.message}")
            }
        }
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionGranted = isGranted
        Log.d("StepsScreen", "Permission result: $isGranted")

        if (isGranted) {
            viewModel.improvedStepManager.startTracking()
            isTracking = true
            Log.d("StepsScreen", "Tracking started")
        } else {
            Log.w("StepsScreen", "Permission denied!")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Pelacak Langkah")

                            if (isWalking && isTracking) {
                                PulsingDot()
                            }
                        }

                        // Sensor status indicator
                        Text(
                            text = sensorStatus,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Kembali")
                    }
                },
                actions = {
                    // Debug info button
                    IconButton(onClick = { showDebugInfo = !showDebugInfo }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Debug Info",
                            tint = if (showDebugInfo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer
                        )
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
                        Log.d("StepsScreen", "Stopping tracking")
                        viewModel.improvedStepManager.stopTracking()
                        isTracking = false
                    } else {
                        Log.d("StepsScreen", "Requesting permission")
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                        } else {
                            viewModel.improvedStepManager.startTracking()
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
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Debug Info Card (collapsible)
                if (showDebugInfo) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "🐛 Debug Info",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = viewModel.getDebugInfo(),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                )
                            }
                        }
                    }
                }

                item {
                    // Main Steps Card
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
                                if (isTracking && isWalking) {
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

                                // Display sensor steps in real-time when tracking
                                Text(
                                    text = if (isTracking) "$sensorSteps" else "${todaySteps?.steps ?: 0}",
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
                                        val steps = if (isTracking) sensorSteps else (todaySteps?.steps ?: 0)
                                        steps.toFloat() / stepGoal.toFloat()
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
                                            Icon(
                                                imageVector = Icons.Default.Sensors,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                "Tracking Aktif • Real-time",
                                                color = Color.White,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Calories Card
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
                                        text = "≈ ${String.format("%.1f", todayCalories / 7.7)}g lemak",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }
                }

                // Rest of the items (Motivational message, history, etc.)
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

            // Floating step indicator
            AnimatedVisibility(
                visible = stepDetected && isTracking,
                enter = scaleIn(initialScale = 0.5f) + fadeIn(),
                exit = scaleOut(targetScale = 1.5f) + fadeOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 8.dp,
                    modifier = Modifier.size(80.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "👟",
                            style = MaterialTheme.typography.displaySmall
                        )
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
            animation = tween(400, easing = LinearEasing),
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
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .size(8.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(Color(0xFF4CAF50))
    )
}