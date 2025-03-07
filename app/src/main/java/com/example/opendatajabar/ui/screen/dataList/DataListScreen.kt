package com.example.opendatajabar.ui.screen.dataList

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.opendatajabar.data.local.DataEntity
import com.example.opendatajabar.viewmodel.DataViewModel
import kotlinx.coroutines.launch

private const val TAG = "DataListScreen"

@Composable
fun DataListScreen(navController: NavHostController, viewModel: DataViewModel) {
    // Tambahkan logcat di awal composable
    Log.d(TAG, "DataListScreen composable started")

    val dataList by viewModel.dataList.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Log status data dan loading
    LaunchedEffect(dataList, isLoading) {
        Log.d(TAG, "Data state changed - isLoading: $isLoading, dataList size: ${dataList.size}")
        if (dataList.isNotEmpty()) {
            Log.d(TAG, "Sample first item: ${dataList.first()}")
        }
    }

    var showDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<DataEntity?>(null) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when {
                isLoading -> {
                    Log.d(TAG, "Showing loading indicator")
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                dataList.isEmpty() -> {
                    Log.d(TAG, "Showing empty state - no data available")
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "No Data Available", style = MaterialTheme.typography.headlineMedium)
                    }
                }
                else -> {
                    Log.d(TAG, "Rendering LazyColumn with ${dataList.size} items")
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        items(dataList) { item ->
                            Log.d(TAG, "Rendering item with id: ${item.id}, name: ${item.namaProvinsi}")
                            DataItemCard(
                                item = item,
                                onEditClick = { navController.navigate("edit/${item.id}") },
                                onDeleteClick = {
                                    selectedItem = item
                                    showDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Dialog konfirmasi hapus data
    DeleteConfirmationDialog(
        showDialog = showDialog,
        onDismiss = { showDialog = false },
        onConfirm = {
            selectedItem?.let {
                Log.d(TAG, "Deleting item with id: ${it.id}")
                viewModel.deleteData(it)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Data berhasil dihapus")
                }
            }
            showDialog = false
        }
    )

    // Tambahkan efek untuk memastikan data dimuat saat composable pertama kali dibuat
    DisposableEffect(Unit) {
        Log.d(TAG, "DataListScreen entered composition, checking if data needs to be fetched")
        // Tambahkan kode di sini jika perlu memastikan data dimuat
        // Misalnya: if (dataList.isEmpty() && !isLoading) viewModel.fetchData()

        onDispose {
            Log.d(TAG, "DataListScreen leaving composition")
        }
    }
}

@Composable
fun DataItemCard(
    item: DataEntity,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            Text(
                text = "Provinsi: ${item.namaProvinsi} (${item.kodeProvinsi})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Kabupaten/Kota: ${item.namaKabupatenKota} (${item.kodeKabupatenKota})",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Total: ${item.rataRataLamaSekolah} ${item.satuan}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tahun: ${item.tahun}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onEditClick,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Edit")
                }

                Spacer(modifier = Modifier.width(5.dp))

                Button(
                    onClick = onDeleteClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(text = "Delete")
                }
            }
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Konfirmasi Hapus") },
            text = { Text("Apakah Anda Yakin Untuk Menghapus Data Ini?") },
            confirmButton = {
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Ya")
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text("Tidak")
                }
            }
        )
    }
}