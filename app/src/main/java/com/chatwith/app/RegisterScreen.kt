package com.chatwith.app

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.fontResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onLoginClick: () -> Unit
) {
    val interFont = FontFamily(Font(R.font.inter_regular))
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var agreeTerms by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 48.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Create your new account", fontSize = 28.sp, fontFamily = interFont)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Create an account to start looking for the food you like",
            fontSize = 14.sp,
            fontFamily = interFont,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address", fontFamily = interFont) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("User Name", fontFamily = interFont) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password", fontFamily = interFont) },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = agreeTerms, onCheckedChange = { agreeTerms = it })
            Text(
                "I agree with Terms of Service and Privacy Policy",
                fontSize = 12.sp,
                fontFamily = interFont,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (!agreeTerms) {
                    Toast.makeText(context, "Please accept the terms", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                isLoading = true

                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val uid = task.result?.user?.uid
                            val userMap = mapOf("email" to email, "username" to username)

                            FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(uid ?: "")
                                .set(userMap)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show()
                                    onRegisterSuccess()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(context, "Firestore error: ${e.message}", Toast.LENGTH_SHORT).show()
                                    Log.e("Register", "Firestore write error", e)
                                }
                        } else {
                            Toast.makeText(
                                context,
                                "Error: ${task.exception?.localizedMessage}",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e("Register", "Auth error", task.exception)
                        }
                        isLoading = false
                    }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            if (isLoading) CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
            else Text("Register", color = Color.White, fontFamily = interFont)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Text("Already have an account? ", fontFamily = interFont)
            Text(
                "Sign In",
                color = Color(0xFF1976D2),
                fontFamily = interFont,
                modifier = Modifier.clickable { onLoginClick() }
            )
        }
    }
}
