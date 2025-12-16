package com.example.stepdrink.ui.screen.water

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.stepdrink.viewmodel.WaterViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterScreen(
    navController: NavController,
    viewModel: WaterViewModel
) {
    val todayRecords by viewModel.todayWaterRecords.collectAsState()
    val totalWater by viewModel.todayTotalWater.collectAsState()
    val waterGoal by viewModel.dailyGoal.collectAsState()

    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pelacak Air Minum") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Kembali")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true }
            ) {
                Icon(Icons.Default.Add, "Tambah")
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
                // Water Progress Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.tertiary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "${totalWater}ml",
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Air Minum Hari Ini",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        LinearProgressIndicator(
                            progress = { totalWater.toFloat() / waterGoal.toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Target: ${waterGoal}ml",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            item {
                // Quick Add Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickAddButton(
                        amount = 250,
                        onClick = { viewModel.addWater(250) },
                        modifier = Modifier.weight(1f)
                    )
                    QuickAddButton(
                        amount = 500,
                        onClick = { viewModel.addWater(500) },
                        modifier = Modifier.weight(1f)
                    )
                    QuickAddButton(
                        amount = 1000,
                        onClick = { viewModel.addWater(1000) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Text(
                    text = "Riwayat Hari Ini",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            items(todayRecords) { record ->
                WaterRecordItem(
                    amount = record.amount,
                    timestamp = record.timestamp,
                    onDelete = { viewModel.deleteWaterRecord(record) }
                )
            }
        }
    }

    if (showDialog) {
        AddWaterDialog(
            onDismiss = { showDialog = false },
            onConfirm = { amount ->
                viewModel.addWater(amount)
                showDialog = false
            }
        )
    }
}

@Composable
fun QuickAddButton(
    amount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.WaterDrop, contentDescription = null)
            Text("${amount}ml")
        }
    }
}

@Composable
fun WaterRecordItem(
    amount: Int,
    timestamp: Long,
    onDelete: () -> Unit
) {
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val time = timeFormat.format(Date(timestamp))

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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.WaterDrop,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Column {
                    Text(
                        text = "${amount}ml",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = time,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Hapus",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun AddWaterDialog(
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var amount by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Air Minum") },
        text = {
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it.filter { char -> char.isDigit() } },
                label = { Text("Jumlah (ml)") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val amountInt = amount.toIntOrNull()
                    if (amountInt != null && amountInt > 0) {
                        onConfirm(amountInt)
                    }
                }
            ) {
                Text("Tambah")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}