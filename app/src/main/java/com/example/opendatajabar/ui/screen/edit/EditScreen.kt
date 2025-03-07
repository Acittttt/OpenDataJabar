package com.example.opendatajabar.ui.screen.edit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.opendatajabar.data.local.DataEntity
import com.example.opendatajabar.viewmodel.DataViewModel

@Composable
fun EditScreen(viewModel: DataViewModel, navController: NavController, dataId: Int) {
    val context = LocalContext.current

    // State untuk input
    var kodeProvinsi by remember { mutableStateOf("") }
    var namaProvinsi by remember { mutableStateOf("") }
    var kodeKabupatenKota by remember { mutableStateOf("") }
    var namaKabupatenKota by remember { mutableStateOf("") }
    var total by remember { mutableStateOf("") }
    var satuan by remember { mutableStateOf("") }
    var tahun by remember { mutableStateOf("") }

    var showDialog by remember { mutableStateOf(false) }
    var isDataLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(dataId) {
        val data = viewModel.getDataById(dataId)
        data?.let {
            kodeProvinsi = it.kodeProvinsi.toString()
            namaProvinsi = it.namaProvinsi
            kodeKabupatenKota = it.kodeKabupatenKota.toString()
            namaKabupatenKota = it.namaKabupatenKota
            total = it.rataRataLamaSekolah.toString()
            satuan = it.satuan
            tahun = it.tahun.toString()
            isDataLoaded = true
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Edit Data",
                style = MaterialTheme.typography.headlineMedium
            )

            OutlinedTextField(
                value = kodeProvinsi,
                onValueChange = {},
                label = { Text("Kode Provinsi") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = namaProvinsi,
                onValueChange = {},
                label = { Text("Nama Provinsi") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = kodeKabupatenKota,
                onValueChange = { kodeKabupatenKota = it },
                label = { Text("Kode Kabupaten/Kota") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = namaKabupatenKota,
                onValueChange = { namaKabupatenKota = it },
                label = { Text("Nama Kabupaten/Kota") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = total,
                onValueChange = { total = it },
                label = { Text("Total") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = satuan,
                onValueChange = { satuan = it },
                label = { Text("Satuan") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = tahun,
                onValueChange = { tahun = it },
                label = { Text("Tahun") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (kodeKabupatenKota.isBlank() || namaKabupatenKota.isBlank() ||
                        total.isBlank() || satuan.isBlank() || tahun.isBlank()
                    ) {
                        showDialog = true
                    } else {
                        viewModel.updateData(
                            DataEntity(
                                id = dataId,
                                kodeProvinsi = kodeProvinsi.toInt(),
                                namaProvinsi = namaProvinsi,
                                kodeKabupatenKota = kodeKabupatenKota.toInt(),
                                namaKabupatenKota = namaKabupatenKota,
                                rataRataLamaSekolah = total.toDouble(),
                                satuan = satuan,
                                tahun = tahun.toInt()
                            )
                        )

                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = isDataLoaded // Hanya aktif jika data sudah dimuat
            ) {
                Text("Simpan Perubahan")
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Button(
                    onClick = { showDialog = false }
                ) {
                    Text("OK")
                }
            },
            title = { Text("Input Tidak Lengkap") },
            text = { Text("Harap isi semua data sebelum menyimpan!") }
        )
    }
}