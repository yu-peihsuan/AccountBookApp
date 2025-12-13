@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.accountbook.screens

import android.Manifest
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Notifications
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.accountbook.viewmodel.TransactionViewModel
import java.io.File
import java.util.Locale


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
    val context = LocalContext.current
    val strings = vm.currentStrings

    // 預算相關狀態
    var tempBudget by remember { mutableStateOf(vm.budget.toString()) }
    var showBudgetDialog by remember { mutableStateOf(false) }

    var showCurrencyDialog by remember { mutableStateOf(false) }

    // 修改名稱相關狀態
    var showNameDialog by remember { mutableStateOf(false) }
    var tempName by remember { mutableStateOf("") }
    var nameErrorMsg by remember { mutableStateOf("") }

    // 刪除帳號確認 Dialog 狀態
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    // 使用說明 Dialog 狀態
    var showHelpDialog by remember { mutableStateOf(false) }

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

    // 權限請求啟動器 (Android 13+)
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                vm.updateReminderEnabled(true)
            } else {
                Toast.makeText(context, strings.msgPermissionRequired, Toast.LENGTH_LONG).show()
                vm.updateReminderEnabled(false)
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

                    Spacer(Modifier.height(12.dp))

                    // 刪除帳號按鈕
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .clickable { showDeleteConfirmDialog = true }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, null, tint = Color.Red, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(strings.labelDeleteAccount, color = Color.Red, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
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

                    // 預算設定
                    Box(
                        Modifier.clickable {
                            tempBudget = vm.budget.toString()
                            showBudgetDialog = true
                        }
                    ) {
                        SettingItemDisplay(label = strings.labelBudget, value = vm.budget.toString())
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

                    // 每日提醒設定
                    val timeStr = String.format(Locale.getDefault(), "%02d:%02d", vm.reminderHour, vm.reminderMinute)

                    Column {
                        Text(strings.labelReminder, color = SettingTextColor, fontSize = 14.sp, modifier = Modifier.padding(bottom = 4.dp))
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .border(1.dp, SettingBorderColor, RoundedCornerShape(8.dp))
                                .background(Color.White, RoundedCornerShape(8.dp))
                                .padding(horizontal = 16.dp, vertical = 6.dp) // 高度調小一點適應 Switch
                        ) {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // 點擊時間文字可開啟時間選擇器 (只有在啟用時)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable(enabled = vm.isReminderEnabled) {
                                        TimePickerDialog(
                                            context,
                                            { _, hour, minute ->
                                                vm.setReminderTime(hour, minute)
                                            },
                                            vm.reminderHour,
                                            vm.reminderMinute,
                                            true // 24小時制
                                        ).show()
                                    }
                                ) {
                                    Icon(Icons.Outlined.Notifications, null, tint = if(vm.isReminderEnabled) SettingTextColor else Color.LightGray)
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = timeStr,
                                        fontSize = 18.sp,
                                        color = if(vm.isReminderEnabled) SettingTextColor else Color.LightGray
                                    )
                                }

                                Switch(
                                    checked = vm.isReminderEnabled,
                                    onCheckedChange = { isChecked ->
                                        if (isChecked) {
                                            // 檢查權限 (Android 13+)
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                                                    vm.updateReminderEnabled(true)
                                                } else {
                                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                                }
                                            } else {
                                                vm.updateReminderEnabled(true)
                                            }
                                        } else {
                                            vm.updateReminderEnabled(false)
                                        }
                                    }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // 測試按鈕 (僅供除錯用，如果不需要可自行註解掉)
                    if (vm.isReminderEnabled) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .clickable {
                                    // 模擬發送廣播，立即觸發接收器
                                    val intent = android.content.Intent(context, com.example.accountbook.receiver.ReminderReceiver::class.java)
                                    context.sendBroadcast(intent)
                                    Toast.makeText(context, "已發送測試通知", Toast.LENGTH_SHORT).show()
                                }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Text("立即測試發送通知", color = Color.Blue, fontSize = 14.sp)
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // 匯出按鈕
                    SettingItemAction(label = strings.labelExport) {
                        vm.exportTransactionData(context)
                    }

                    Spacer(Modifier.height(12.dp))

                    // ★ 移除語言顯示欄位

                    // 使用說明按鈕事件
                    SettingItemAction(label = strings.labelHelp) {
                        showHelpDialog = true
                    }
                }

                Spacer(Modifier.height(10.dp))
            }

            // ======= 底部滿版紅色 登出按鈕 =======
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
                    strings.menuLogout,
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

    // ====== 預算修改 Dialog ======
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

    // 刪除帳號確認 Dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text(strings.titleDeleteConfirm, fontWeight = FontWeight.Bold, color = Color.Red, fontSize = 18.sp) },
            text = { Text(strings.msgDeleteConfirm) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmDialog = false
                        vm.deleteCurrentAccount()
                        onLogout() // 觸發登出導航
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) { Text(strings.btnDelete) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) { Text(strings.btnCancel, color = Color.Gray) }
            },
            containerColor = Color.White
        )
    }

    // 使用說明 Dialog
    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = { Text(strings.helpTitle, fontWeight = FontWeight.Bold) },
            text = {
                // 使用 Box 和 verticalScroll 來確保長篇文字可以捲動
                Box(modifier = Modifier.heightIn(max = 400.dp)) {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        Text(strings.helpContent, lineHeight = 20.sp, fontSize = 15.sp)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) { Text(strings.btnConfirm) }
            },
            containerColor = Color.White
        )
    }
}

// ... (SettingSection, SettingItemDisplay 等 Helper Composable 維持不變)
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
fun SettingItemAction(label: String, onClick: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .border(1.dp, SettingBorderColor, RoundedCornerShape(8.dp))
            .background(Color.White, RoundedCornerShape(8.dp))
            .clickable { onClick() }
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