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

    var showDialog by remember { mutableStateOf(false) } // State untuk pop-up error

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
                onValueChange = { kodeProvinsi = it },
                label = { Text("Kode Provinsi") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = namaProvinsi,
                onValueChange = { namaProvinsi = it },
                label = { Text("Nama Provinsi") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = kodeKabupatenKota,
                onValueChange = { kodeKabupatenKota = it },
                label = { Text("Kode Kabupaten/Kota") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = namaKabupatenKota,
                onValueChange = { namaKabupatenKota = it },
                label = { Text("Nama Kabupaten/Kota") },
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
                    if (kodeProvinsi.isBlank() || namaProvinsi.isBlank() ||
                        kodeKabupatenKota.isBlank() || namaKabupatenKota.isBlank() ||
                        total.isBlank() || satuan.isBlank() || tahun.isBlank()
                    ) {
                        showDialog = true
                    } else {
                        viewModel.insertData(
                            kodeProvinsi = kodeProvinsi.toIntOrNull() ?: 0,
                            namaProvinsi = namaProvinsi,
                            kodeKabupatenKota = kodeKabupatenKota.toIntOrNull() ?: 0,
                            namaKabupatenKota = namaKabupatenKota,
                            total = total.toDoubleOrNull() ?: 0.0,
                            satuan = satuan,
                            tahun = tahun.toIntOrNull() ?: 0
                        )

                        // Reset input setelah submit
                        kodeProvinsi = ""
                        namaProvinsi = ""
                        kodeKabupatenKota = ""
                        namaKabupatenKota = ""
                        total = ""
                        satuan = ""
                        tahun = ""

                        // Navigasi kembali setelah data disimpan
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Simpan Perubahan")
            }
        }
    }

    // Dialog error jika ada input kosong
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