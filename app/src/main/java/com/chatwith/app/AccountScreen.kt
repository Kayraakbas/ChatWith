package com.chatwith.app

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    val user = auth.currentUser
    email = user?.email ?: ""

    LaunchedEffect(Unit) {
        val uid = user?.uid
        if (uid != null) {
            FirebaseFirestore.getInstance().collection("users")
                .document(uid)
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null && snapshot.exists()) {
                        username = snapshot.getString("username") ?: ""
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

            Image(
                painter = painterResource(id = R.drawable.ic_profile_placeholder),
                contentDescription = "Profile Picture",
                modifier = Modifier.size(100.dp)
            )

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
