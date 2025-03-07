package com.example.opendatajabar.ui.screen.profile

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.opendatajabar.viewmodel.ProfileViewModel
import java.util.regex.Pattern

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: ProfileViewModel) {
    val profile by viewModel.profile.collectAsState()
    val context = LocalContext.current
    var isEditing by remember { mutableStateOf(false) }
    var studentName by remember { mutableStateOf(profile?.name ?: "Mahasiswa JTK") }
    var studentId by remember { mutableStateOf(profile?.studentId ?: "22222") }
    var studentEmail by remember { mutableStateOf(profile?.email ?: "mahasiswa@jtk.polban.ac.id") }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var hasChanges by remember { mutableStateOf(false) }

    val profileImageBitmap = profile?.image?.let { imageData ->
        BitmapFactory.decodeByteArray(imageData, 0, imageData.size)?.asImageBitmap()
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        profileImageUri = uri
        uri?.let { viewModel.updateProfileImage(it, context) }
        hasChanges = true
    }

    fun rollbackChanges() {
        studentName = profile?.name ?: "Mahasiswa JTK"
        studentId = profile?.studentId ?: "22222"
        studentEmail = profile?.email ?: "mahasiswa@jtk.polban.ac.id"
        isEditing = false
        hasChanges = false
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Konfirmasi") },
            text = { Text("Apakah Anda yakin untuk membatalkan perubahan?") },
            confirmButton = {
                Button(
                    onClick = {
                        rollbackChanges()
                        showDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Ya", color = Color.White)
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Tidak")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isEditing && hasChanges) {
                            showDialog = true
                        } else {
                            isEditing = false
                        }
                    }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Card(
                    modifier = Modifier
                        .size(160.dp)
                        .clickable { if (isEditing) imagePickerLauncher.launch("image/*") },
                    shape = CircleShape,
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    if (profileImageBitmap != null) {
                        Image(
                            bitmap = profileImageBitmap,
                            contentDescription = "Profile Picture",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Default Profile Picture",
                                tint = Color.Gray,
                                modifier = Modifier.size(100.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (isEditing) {
                    OutlinedTextField(
                        value = studentName,
                        onValueChange = {
                            studentName = it
                            hasChanges = true
                        },
                        label = { Text("Student Name") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                    )

                    OutlinedTextField(
                        value = studentId,
                        onValueChange = {
                            studentId = it
                            hasChanges = true
                        },
                        label = { Text("Student ID") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    OutlinedTextField(
                        value = studentEmail,
                        onValueChange = {
                            studentEmail = it
                            hasChanges = true
                        },
                        label = { Text("Student Email") },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text(text = studentName, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "NIM: $studentId", fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = studentEmail, fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isEditing) {
                    Button(
                        onClick = {
                            viewModel.updateProfile(studentName, studentId, studentEmail)
                            isEditing = false
                            hasChanges = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save")
                    }
                } else {
                    Button(
                        onClick = { isEditing = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Edit Profile")
                    }
                }
            }
        }
    }
}