package com.chatwith.app

import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import androidx.compose.material.icons.filled.List


@Composable
fun AccountScreen(
    onLogout: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onChatHistoryClick: () -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var profileImageUrl by remember { mutableStateOf<String?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    val user = auth.currentUser
    email = user?.email ?: ""
    val uid = user?.uid

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedImageUri ->
            isUploading = true
            val storageRef = storage.reference.child("profile_images/${uid}")

            storageRef.putFile(selectedImageUri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        val imageUrl = downloadUrl.toString()

                        // Firestore'da profil resmi URL'sini güncelle
                        if (uid != null) {
                            firestore.collection("users").document(uid)
                                .update("profileImageUrl", imageUrl)
                                .addOnSuccessListener {
                                    profileImageUrl = imageUrl
                                    isUploading = false
                                    Toast.makeText(context, "Profil fotoğrafı güncellendi", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    isUploading = false
                                    Toast.makeText(context, "Profil fotoğrafı güncellenemedi", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                }
                .addOnFailureListener {
                    isUploading = false
                    Toast.makeText(context, "Fotoğraf yüklenemedi", Toast.LENGTH_SHORT).show()
                }
        }
    }

    LaunchedEffect(Unit) {
        if (uid != null) {
            firestore.collection("users")
                .document(uid)
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null && snapshot.exists()) {
                        username = snapshot.getString("username") ?: ""
                        profileImageUrl = snapshot.getString("profileImageUrl")
                    }
                }
        }
    }

    // Tema renklerini çek
    val colors = MaterialTheme.colorScheme

    Surface(color = colors.background, modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Text(
                "Profile",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onBackground
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier.size(100.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                if (profileImageUrl != null && profileImageUrl!!.isNotEmpty()) {
                    // Profil fotoğrafını Coil ile yükle
                    Image(
                        painter = rememberAsyncImagePainter(profileImageUrl),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .border(2.dp, colors.primary, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Varsayılan profil fotoğrafı
                    Image(
                        painter = painterResource(id = R.drawable.ic_profile_placeholder),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .border(2.dp, colors.primary, CircleShape)
                    )
                }

                // Kamera ikonu ekleme
                IconButton(
                    onClick = { imagePicker.launch("image/*") },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(colors.primary)
                        .border(1.dp, colors.background, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Change Photo",
                        tint = colors.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (isUploading) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator()
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(username, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = colors.onBackground)
            Text(email, fontSize = 14.sp, color = colors.onSurface)

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        context.startActivity(Intent(context, EditProfileActivity::class.java))
                    }
            ) {
                ListItem(
                    headlineContent = { Text("Personal Data", color = colors.onSurface) },
                    leadingContent = {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Icon", tint = colors.onSurface)
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onChatHistoryClick()
                    }
            ) {
                ListItem(
                    headlineContent = { Text("Chat History", color = colors.onSurface) },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.List,
                            contentDescription = "Chat History",
                            tint = colors.onSurface
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Dark Mode", fontSize = 16.sp, color = colors.onBackground)
                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = { onToggleTheme() }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    auth.signOut()
                    onLogout()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
            ) {
                Text("Sign Out", color = colors.onPrimary)
            }
        }
    }
}
