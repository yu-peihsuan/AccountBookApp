@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.accountbook.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.accountbook.viewmodel.TransactionViewModel

val SettingBgColor = Color(0xFFFDFBF7)

val SettingTopBarColor = Color(0xFFFCF5E8)
val SettingTextColor = Color(0xFF4A463F)
val SettingBorderColor = Color(0xFFAFAFAF)

@Composable
fun SettingScreen(
    vm: TransactionViewModel,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onOpenDrawer: () -> Unit // 選單抽屜觸發
) {
    val strings = vm.currentStrings
    var tempBudget by remember { mutableStateOf(vm.budget.toString()) }
    val focusManager = LocalFocusManager.current

    var showCurrencyDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = SettingBgColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(strings.settingTitle, color = SettingTextColor, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {  // 顯示側邊menu
                        Icon(Icons.Default.Menu, null, tint = SettingTextColor)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = SettingTopBarColor)
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween // 讓登出保持在最底部
        ) {

            // ====== 上半部設定區塊 ======
            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {

                // ============= 帳號管理 =============
                SettingSection(title = strings.sectionAccount, icon = Icons.Outlined.Person) {
                    SettingItemDisplay(label = strings.labelAccount, value = vm.userEmail.ifEmpty { "N/A" })
                    Spacer(Modifier.height(12.dp))
                    SettingItemDisplay(label = strings.labelName, value = vm.userName.ifEmpty { "N/A" })
                }

                // ============= 系統設定 =============
                SettingSection(title = strings.sectionFunction, icon = Icons.Outlined.Settings) {

                    // ★幣別切換
                    SettingItemValue(
                        label = strings.labelCurrency,
                        value = vm.currency,
                        onClick = { showCurrencyDialog = true }
                    )

                    Spacer(Modifier.height(12.dp))

                    // ★預算輸入框
                    Text(strings.labelBudget, color = SettingTextColor, fontSize = 14.sp, modifier = Modifier.padding(bottom = 4.dp))
                    OutlinedTextField(
                        value = tempBudget,
                        onValueChange = { if (it.all { char -> char.isDigit() }) tempBudget = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = SettingBorderColor,
                            unfocusedBorderColor = SettingBorderColor,
                            cursorColor = SettingTextColor,
                            focusedTextColor = SettingTextColor,
                            unfocusedTextColor = SettingTextColor
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = {
                                val newBudget = tempBudget.toIntOrNull() ?: 0
                                vm.updateBudget(newBudget)
                                focusManager.clearFocus()
                            }) {
                                Icon(Icons.Default.Check, contentDescription = "Save", tint = Color(0xFF4CAF50))
                            }
                        }
                    )

                    Spacer(Modifier.height(12.dp))

                    SettingItemAction(label = strings.labelExport)

                    Spacer(Modifier.height(12.dp))

                    SettingItemDisplay(label = strings.labelLanguage, value = "中文(繁體)")  // (固定顯示)

                    Spacer(Modifier.height(12.dp))
                    SettingItemAction(label = strings.labelHelp)
                }
            }

            // ======= 🔥 底部滿版紅色 登出按鈕 (與圖片一樣) =======
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))   // 圓角更大、更接近圖片
                    .background(Color(0xFFE57373))     // 圖片中柔和飽和度的紅色
                    .clickable { onLogout() }
                    .padding(vertical = 16.dp),        // 高度一致
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "登出",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White               // 文字白色 ✔
                )
            }


        }
    }

    // ====== 幣別 Dialog ======
    if (showCurrencyDialog) {
        SelectionDialog(
            title = strings.labelCurrency,
            options = listOf("台幣 (NT$)", "美金 ($)", "日幣 (¥)"),
            onDismiss = { showCurrencyDialog = false },
            onSelect = { selected ->
                vm.updateCurrency(
                    when {
                        selected.contains("NT$") -> "NT$"
                        selected.contains("$") -> "$"
                        selected.contains("¥") -> "¥"
                        else -> "NT$"
                    }
                )
                showCurrencyDialog = false
            },
            strings = strings
        )
    }
}

@Composable
fun SettingSection(title: String, icon: ImageVector, content: @Composable () -> Unit) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
            Icon(icon, null, tint = SettingTextColor, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(8.dp))
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = SettingTextColor)
        }
        content()
    }
}

@Composable
fun SettingItemDisplay(label: String, value: String) {
    Column {
        if (label.isNotEmpty()) Text(label, color = SettingTextColor, fontSize = 14.sp, modifier = Modifier.padding(bottom = 4.dp))

        Box(
            Modifier
                .fillMaxWidth()
                .border(1.dp, SettingBorderColor, RoundedCornerShape(8.dp))
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) { Text(value, color = Color.Gray, fontSize = 16.sp) }
    }
}

@Composable
fun SettingItemValue(label: String, value: String, onClick: () -> Unit) {
    Column {
        Text(label, color = SettingTextColor, fontSize = 14.sp, modifier = Modifier.padding(bottom = 4.dp))

        Box(
            Modifier
                .fillMaxWidth()
                .border(1.dp, SettingBorderColor, RoundedCornerShape(8.dp))
                .background(Color.White, RoundedCornerShape(8.dp))
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(value, color = SettingTextColor, fontSize = 16.sp)
                Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun SettingItemAction(label: String) {
    Box(
        Modifier
            .fillMaxWidth()
            .border(1.dp, SettingBorderColor, RoundedCornerShape(8.dp))
            .background(Color.White, RoundedCornerShape(8.dp))
            .clickable { /* TODO */ }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, color = SettingTextColor, fontSize = 16.sp)
            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun SelectionDialog(title: String, options: List<String>, onDismiss: () -> Unit, onSelect: (String) -> Unit, strings: com.example.accountbook.viewmodel.StringResources) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                options.forEach { option ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(option) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) { Text(option, fontSize = 16.sp) }

                    if (option != options.last()) HorizontalDivider(color = Color.LightGray)
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text(strings.btnCancel) } }
    )
}
