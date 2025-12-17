package com.example.stepdrink.ui.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.stepdrink.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel
) {
    val userName by viewModel.userName.collectAsState()
    val stepGoal by viewModel.stepGoal.collectAsState()
    val waterGoal by viewModel.waterGoal.collectAsState()
    val userWeight by viewModel.userWeight.collectAsState()  // TAMBAH INI

    var showNameDialog by remember { mutableStateOf(false) }
    var showStepGoalDialog by remember { mutableStateOf(false) }
    var showWaterGoalDialog by remember { mutableStateOf(false) }
    var showWeightDialog by remember { mutableStateOf(false) }  // TAMBAH INI

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil & Pengaturan") },
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Header dengan Gradient
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
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    MaterialTheme.colorScheme.secondaryContainer
                                )
                            )
                        )
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(50.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        Text(
                            text = userName,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )

                        // Stats Mini Card
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            StatsChip(
                                icon = Icons.Default.DirectionsWalk,
                                label = "Target",
                                value = "$stepGoal"
                            )
                            StatsChip(
                                icon = Icons.Default.WaterDrop,
                                label = "Target",
                                value = "${waterGoal}ml"
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Settings Section
            Text(
                text = "Informasi Pribadi",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            )

            // Name Setting
            SettingItem(
                icon = Icons.Default.Person,
                title = "Nama Lengkap",
                value = userName,
                iconTint = MaterialTheme.colorScheme.primary,
                onClick = { showNameDialog = true }
            )

            // TAMBAH INI - Weight Setting
            SettingItem(
                icon = Icons.Default.FitnessCenter,
                title = "Berat Badan",
                value = "$userWeight kg",
                iconTint = Color(0xFFFF6B6B),
                onClick = { showWeightDialog = true }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Target Harian",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            )

            // Step Goal Setting
            SettingItem(
                icon = Icons.Default.DirectionsWalk,
                title = "Target Langkah",
                value = "$stepGoal langkah/hari",
                iconTint = Color(0xFF4CAF50),
                onClick = { showStepGoalDialog = true }
            )

            // Water Goal Setting
            SettingItem(
                icon = Icons.Default.WaterDrop,
                title = "Target Air Minum",
                value = "${waterGoal}ml/hari",
                iconTint = Color(0xFF2196F3),
                onClick = { showWaterGoalDialog = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Column {
                        Text(
                            text = "Tentang Aplikasi",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Step & Drink v1.0\nAplikasi tracking langkah harian dan kebutuhan air minum dengan perhitungan kalori.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    // Dialogs
    if (showNameDialog) {
        EditTextDialog(
            title = "Edit Nama",
            label = "Nama Lengkap",
            currentValue = userName,
            onDismiss = { showNameDialog = false },
            onConfirm = { newName ->
                viewModel.updateUserName(newName)
                showNameDialog = false
            }
        )
    }

    // TAMBAH INI - Weight Dialog
    if (showWeightDialog) {
        EditNumberDialog(
            title = "Edit Berat Badan",
            label = "Berat (kg)",
            currentValue = userWeight,
            onDismiss = { showWeightDialog = false },
            onConfirm = { newWeight ->
                viewModel.updateUserWeight(newWeight)
                showWeightDialog = false
            }
        )
    }

    if (showStepGoalDialog) {
        EditNumberDialog(
            title = "Edit Target Langkah",
            label = "Target (langkah/hari)",
            currentValue = stepGoal,
            onDismiss = { showStepGoalDialog = false },
            onConfirm = { newGoal ->
                viewModel.updateStepGoal(newGoal)
                showStepGoalDialog = false
            }
        )
    }

    if (showWaterGoalDialog) {
        EditNumberDialog(
            title = "Edit Target Air Minum",
            label = "Target (ml/hari)",
            currentValue = waterGoal,
            onDismiss = { showWaterGoalDialog = false },
            onConfirm = { newGoal ->
                viewModel.updateWaterGoal(newGoal)
                showWaterGoalDialog = false
            }
        )
    }
}

@Composable
fun StatsChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun SettingItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
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
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = CircleShape,
                    color = iconTint.copy(alpha = 0.1f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Edit",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun EditTextDialog(
    title: String,
    label: String,
    currentValue: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf(currentValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(Icons.Default.Edit, contentDescription = null)
        },
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text(label) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (text.isNotBlank()) {
                        onConfirm(text)
                    }
                }
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Composable
fun EditNumberDialog(
    title: String,
    label: String,
    currentValue: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var number by remember { mutableStateOf(currentValue.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(Icons.Default.Edit, contentDescription = null)
        },
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = number,
                onValueChange = { number = it.filter { char -> char.isDigit() } },
                label = { Text(label) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    val value = number.toIntOrNull()
                    if (value != null && value > 0) {
                        onConfirm(value)
                    }
                }
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}