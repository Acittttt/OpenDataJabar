package com.example.opendatajabar.ui.screen.profile

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
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

@Composable
fun ProfileScreen(viewModel: ProfileViewModel) {
    val profile by viewModel.profile.collectAsState()
    val context = LocalContext.current
    var isEditing by remember { mutableStateOf(false) }
    var studentName by remember { mutableStateOf(profile?.name ?: "Mahasiswa JTK") }
    var studentId by remember { mutableStateOf(profile?.studentId ?: "22222") }
    var studentEmail by remember { mutableStateOf(profile?.email ?: "mahasiswa@jtk.polban.ac.id") }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var idError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }

    val profileImageBitmap = profile?.image?.let { imageData ->
        BitmapFactory.decodeByteArray(imageData, 0, imageData.size)?.asImageBitmap()
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        profileImageUri = uri
        uri?.let { viewModel.updateProfileImage(it, context) }
    }

    fun validateInputs(): Boolean {
        var isValid = true

        if (!Pattern.matches("^[A-Za-z ]+$", studentName)) {
            nameError = "Name should contain only letters"
            isValid = false
        } else {
            nameError = null
        }

        if (!Pattern.matches("^\\d+$", studentId)) {
            idError = "Student ID should contain only numbers"
            isValid = false
        } else {
            idError = null
        }

        if (!Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", studentEmail)) {
            emailError = "Invalid email format"
            isValid = false
        } else {
            emailError = null
        }

        return isValid
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
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

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isEditing) {
                        OutlinedTextField(
                            value = studentName,
                            onValueChange = {
                                if (it.all { char -> char.isLetter() || char.isWhitespace() }) {
                                    studentName = it
                                }
                            },
                            label = { Text("Student Name") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                        )

                        OutlinedTextField(
                            value = studentId,
                            onValueChange = {
                                if (it.all { char -> char.isDigit() }) {
                                    studentId = it
                                }
                            },
                            label = { Text("Student ID") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        OutlinedTextField(
                            value = studentEmail,
                            onValueChange = { studentEmail = it },
                            label = { Text("Student Email") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = !android.util.Patterns.EMAIL_ADDRESS.matcher(studentEmail).matches()
                        )

                        if (emailError != null) {
                            Text(emailError!!, color = Color.Red, fontSize = 12.sp)
                        }
                    } else {
                        Text(
                            text = studentName,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "NIM: $studentId",
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = studentEmail,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isEditing) {
                Button(
                    onClick = {
                        if (android.util.Patterns.EMAIL_ADDRESS.matcher(studentEmail).matches()) {
                            isEditing = false
                            viewModel.updateProfile(studentName, studentId, studentEmail)
                        }
                    },
                    enabled = android.util.Patterns.EMAIL_ADDRESS.matcher(studentEmail).matches(),
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