package com.example.stepdrink.ui.screen.steps

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    var isTracking by remember { mutableStateOf(false) }
    var permissionGranted by remember { mutableStateOf(false) }

    // Permission launcher untuk Android 10+
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
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
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
                }
            ) {
                Icon(
                    imageVector = if (isTracking) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = if (isTracking) "Stop" else "Start"
                )
            }
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
                // Current Steps Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsWalk,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "${todaySteps?.steps ?: 0}",
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Langkah Hari Ini",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        LinearProgressIndicator(
                            progress = {
                                (todaySteps?.steps ?: 0).toFloat() / stepGoal.toFloat()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Target: $stepGoal langkah",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        if (isTracking) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Badge(
                                containerColor = MaterialTheme.colorScheme.tertiary
                            ) {
                                Text("Sedang Melacak: $sensorSteps langkah")
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Riwayat 7 Hari Terakhir",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            items(last7Days) { record ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = record.date,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${record.steps} langkah",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        CircularProgressIndicator(
                            progress = { (record.steps.toFloat() / stepGoal.toFloat()).coerceIn(0f, 1f) },
                            modifier = Modifier.size(48.dp),
                        )
                    }
                }
            }
        }
    }
}