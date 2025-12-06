@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.accountbook.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.accountbook.viewmodel.TransactionViewModel
import java.io.File

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

    // 預算相關狀態
    var tempBudget by remember { mutableStateOf(vm.budget.toString()) }
    var showBudgetDialog by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    var showCurrencyDialog by remember { mutableStateOf(false) }

    // 修改名稱相關狀態
    var showNameDialog by remember { mutableStateOf(false) }
    var tempName by remember { mutableStateOf("") }
    var nameErrorMsg by remember { mutableStateOf("") }

    // 捲動狀態
    val scrollState = rememberScrollState()

    // 圖片選擇器
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                vm.updateUserAvatar(uri)
            }
        }
    )

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
                .padding(24.dp)
        ) {

            // ====== 上半部設定區塊 (可捲動) ======
            Column(
                modifier = Modifier
                    .weight(1f) // 佔據剩餘空間
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                // ============= 帳號管理 =============
                SettingSection(title = strings.sectionAccount, icon = Icons.Outlined.Person) {

                    // 大頭照設定區塊
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(1.dp, SettingBorderColor, CircleShape)
                                .clickable {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (vm.userAvatar.isNotEmpty()) {
                                AsyncImage(
                                    model = File(vm.userAvatar),
                                    contentDescription = "User Avatar",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Outlined.Person,
                                    contentDescription = "Default Avatar",
                                    modifier = Modifier.size(60.dp),
                                    tint = Color.Gray
                                )
                            }
                        }
                        // 編輯圖示
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = (-110).dp)
                                .background(SettingTextColor, CircleShape)
                                .padding(6.dp)
                        ) {
                            Icon(Icons.Default.Edit, null, tint = Color.White, modifier = Modifier.size(12.dp))
                        }
                    }

                    SettingItemDisplay(label = strings.labelAccount, value = vm.userEmail.ifEmpty { "N/A" })
                    Spacer(Modifier.height(12.dp))

                    // 名稱顯示與編輯
                    Box(
                        Modifier.clickable {
                            tempName = vm.userName
                            nameErrorMsg = ""
                            showNameDialog = true
                        }
                    ) {
                        SettingItemDisplay(label = strings.labelName, value = vm.userName.ifEmpty { "N/A" })
                        // 編輯提示圖示
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color.Gray,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 16.dp, top = 20.dp)
                                .size(16.dp)
                        )
                    }
                }

                // ============= 系統設定 =============
                SettingSection(title = strings.sectionFunction, icon = Icons.Outlined.Settings) {

                    // 幣別切換
                    SettingItemValue(
                        label = strings.labelCurrency,
                        value = vm.currency,
                        onClick = { showCurrencyDialog = true }
                    )

                    Spacer(Modifier.height(12.dp))

                    // ★ 修改：預算設定 (改成 Dialog 模式，與名稱修改一致)
                    Box(
                        Modifier.clickable {
                            tempBudget = vm.budget.toString()
                            showBudgetDialog = true
                        }
                    ) {
                        SettingItemDisplay(label = strings.labelBudget, value = vm.budget.toString())
                        // 編輯提示圖示
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color.Gray,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 16.dp, top = 20.dp)
                                .size(16.dp)
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    SettingItemAction(label = strings.labelExport)

                    Spacer(Modifier.height(12.dp))

                    SettingItemDisplay(label = strings.labelLanguage, value = "中文(繁體)")

                    Spacer(Modifier.height(12.dp))
                    SettingItemAction(label = strings.labelHelp)
                }

                Spacer(Modifier.height(10.dp))
            }

            // ======= 🔥 底部滿版紅色 登出按鈕 (固定在下方) =======
            Spacer(Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFE57373))
                    .clickable { onLogout() }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "登出",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
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

    // ====== 修改名稱 Dialog ======
    if (showNameDialog) {
        AlertDialog(
            onDismissRequest = { showNameDialog = false },
            title = { Text("修改名稱", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        singleLine = true,
                        label = { Text("新名稱") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (nameErrorMsg.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Text(nameErrorMsg, color = Color.Red, fontSize = 14.sp)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val result = vm.updateUserName(tempName)
                    if (result.isEmpty()) {
                        showNameDialog = false
                    } else {
                        nameErrorMsg = result
                    }
                }) { Text("確定") }
            },
            dismissButton = {
                TextButton(onClick = { showNameDialog = false }) { Text("取消", color = Color.Gray) }
            },
            containerColor = Color.White
        )
    }

    // ★ 修改：新增預算修改 Dialog
    if (showBudgetDialog) {
        AlertDialog(
            onDismissRequest = { showBudgetDialog = false },
            title = { Text(strings.labelBudget, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = tempBudget,
                        onValueChange = { if (it.all { char -> char.isDigit() }) tempBudget = it },
                        singleLine = true,
                        label = { Text(strings.budget) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val newBudget = tempBudget.toIntOrNull() ?: 0
                    vm.updateBudget(newBudget)
                    showBudgetDialog = false
                }) { Text("確定") }
            },
            dismissButton = {
                TextButton(onClick = { showBudgetDialog = false }) { Text("取消", color = Color.Gray) }
            },
            containerColor = Color.White
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