@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.accountbook.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.accountbook.viewmodel.TransactionViewModel

// 定義顏色
val LoginBgColor = Color(0xFFFDFBF7)
val LoginPrimaryColor = Color(0xFF4A463F)

@Composable
fun LoginScreen(
    vm: TransactionViewModel,
    onLoginSuccess: () -> Unit
) {
    // 取得當前語言資源
    val strings = vm.currentStrings

    var isRegisterMode by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Scaffold(
        containerColor = LoginBgColor
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Logo 或標題
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFEAC45D)),
                contentAlignment = Alignment.Center
            ) {
                Text("💰", fontSize = 40.sp)
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = if (isRegisterMode) strings.registerTitle else strings.loginTitle,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = LoginPrimaryColor
            )

            Spacer(Modifier.height(32.dp))

            // 註冊時才顯示姓名欄位
            if (isRegisterMode) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(strings.fieldName) },
                    leadingIcon = { Icon(Icons.Default.Person, null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(16.dp))
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(strings.fieldEmail) },
                leadingIcon = { Icon(Icons.Default.Email, null) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(strings.fieldPassword) },
                leadingIcon = { Icon(Icons.Default.Lock, null) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true
            )

            if (errorMessage.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(errorMessage, color = Color.Red, fontSize = 14.sp)
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank() || (isRegisterMode && name.isBlank())) {
                        errorMessage = strings.errorEmptyField
                    } else {
                        // 呼叫 ViewModel 進行登入/註冊
                        if (isRegisterMode) {
                            vm.register(name, email, password)
                            onLoginSuccess()
                        } else {
                            if (vm.login(email, password)) {
                                onLoginSuccess()
                            } else {
                                errorMessage = strings.errorLoginFailed
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LoginPrimaryColor)
            ) {
                Text(
                    text = if (isRegisterMode) strings.btnRegister else strings.btnLogin,
                    fontSize = 18.sp
                )
            }

            Spacer(Modifier.height(16.dp))

            TextButton(onClick = {
                isRegisterMode = !isRegisterMode
                errorMessage = ""
            }) {
                Text(
                    text = if (isRegisterMode) strings.switchLogin else strings.switchRegister,
                    color = Color.Gray
                )
            }
        }
    }
}