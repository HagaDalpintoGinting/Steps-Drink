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

    var showNameDialog by remember { mutableStateOf(false) }
    var showStepGoalDialog by remember { mutableStateOf(false) }
    var showWaterGoalDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Kembali")
                    }
                }
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
            // Profile Header
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = userName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Settings Section
            Text(
                text = "Pengaturan",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )

            // Name Setting
            SettingItem(
                icon = Icons.Default.Person,
                title = "Nama",
                value = userName,
                onClick = { showNameDialog = true }
            )

            // Step Goal Setting
            SettingItem(
                icon = Icons.Default.DirectionsWalk,
                title = "Target Langkah Harian",
                value = "$stepGoal langkah",
                onClick = { showStepGoalDialog = true }
            )

            // Water Goal Setting
            SettingItem(
                icon = Icons.Default.WaterDrop,
                title = "Target Air Minum Harian",
                value = "${waterGoal}ml",
                onClick = { showWaterGoalDialog = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "Tentang Aplikasi",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Step & Drink v1.0\nAplikasi untuk tracking langkah harian dan kebutuhan air minum.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }

    // Dialogs
    if (showNameDialog) {
        EditTextDialog(
            title = "Edit Nama",
            label = "Nama",
            currentValue = userName,
            onDismiss = { showNameDialog = false },
            onConfirm = { newName ->
                viewModel.updateUserName(newName)
                showNameDialog = false
            }
        )
    }

    if (showStepGoalDialog) {
        EditNumberDialog(
            title = "Edit Target Langkah",
            label = "Target (langkah)",
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
            label = "Target (ml)",
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
fun SettingItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
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
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.Edit,
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
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text(label) },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
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
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = number,
                onValueChange = { number = it.filter { char -> char.isDigit() } },
                label = { Text(label) },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
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