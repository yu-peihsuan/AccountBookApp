@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.accountbook.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

import com.example.accountbook.viewmodel.TransactionViewModel

val BgColor = Color(0xFFFDFBF7)
val SurfaceColor = Color(0xFFF7F2E9)
val AccentYellow = Color(0xFFFBEFC4)
val BorderColor = Color(0xFFE0Ddd5)
val PinkButtonColor = Color(0xFFEBC4C4)
val TextColor = Color(0xFF4A463F)

data class CategoryItem(val name: String, val icon: ImageVector, val key: String)

@Composable
fun AddTransactionScreen(
    vm: TransactionViewModel,
    transactionId: Long,
    onBack: () -> Unit
) {
    val strings = vm.currentStrings
    val currency = vm.currency

    // 判斷是否為編輯模式
    val isEditMode = transactionId != -1L

    // 預設固定為「支出」
    var type by remember { mutableStateOf("支出") }
    // 預設選擇
    var selectedCategoryKey by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("0") }
    var note by remember { mutableStateOf("") }
    var dateMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }

    // 新增類別相關 State
    var showNewCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }

    // 初始化載入資料
    LaunchedEffect(transactionId) {
        if (isEditMode) {
            val tx = vm.getTransactionById(transactionId)
            if (tx != null) {
                type = when(tx.type) {
                    "Expense" -> "支出"
                    "Income" -> "收入"
                    else -> tx.type
                }
                selectedCategoryKey = tx.categoryKey
                amount = tx.amount.toString()
                note = tx.title

                try {
                    val format = SimpleDateFormat(strings.dateFormat, Locale.TAIWAN)
                    val dateObj = format.parse(tx.date)
                    if (dateObj != null) {
                        dateMillis = dateObj.time
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun formatDate(millis: Long): String {
        val format = SimpleDateFormat("${strings.dateFormat} ${strings.dayFormat}", Locale.TAIWAN)
        return format.format(Date(millis))
    }

    fun addDays(days: Int) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateMillis
        calendar.add(Calendar.DAY_OF_YEAR, days)
        dateMillis = calendar.timeInMillis
    }

    // ★ 定義支出類別列表 (已更新 Icon)
    val expenseCategories = listOf(
        CategoryItem(strings.categoryBreakfast, Icons.Filled.FreeBreakfast, "breakfast"),
        CategoryItem(strings.categoryLunch, Icons.Filled.LunchDining, "lunch"),
        CategoryItem(strings.categoryDinner, Icons.Filled.Restaurant, "dinner"),
        CategoryItem(strings.categoryDrink, Icons.Filled.LocalCafe, "drink"),
        CategoryItem(strings.categorySnack, Icons.Filled.Cookie, "snack"),
        CategoryItem(strings.categoryTraffic, Icons.Filled.DirectionsCar, "traffic"),
        CategoryItem(strings.categoryShopping, Icons.Filled.ShoppingBag, "shopping"),
        CategoryItem(strings.categoryDaily, Icons.Filled.LocalGroceryStore, "daily"),
        CategoryItem(strings.categoryRent, Icons.Filled.Home, "rent"),
        CategoryItem(strings.categoryEntertainment, Icons.Filled.SportsEsports, "entertainment"),
        CategoryItem(strings.categoryBills, Icons.Filled.ReceiptLong, "bills"),
        CategoryItem(strings.categoryOther, Icons.Filled.MoreHoriz, "other")
    )

    // ★ 定義收入類別列表 (已更新 Icon)
    val incomeCategories = listOf(
        CategoryItem(strings.categorySalary, Icons.Filled.AttachMoney, "salary"),
        CategoryItem(strings.categoryBonus, Icons.Filled.EmojiEvents, "bonus"),
        CategoryItem(strings.categoryRewards, Icons.Filled.Redeem, "rewards"),
        CategoryItem(strings.categoryInvoice, Icons.Filled.Receipt, "invoice"),
        CategoryItem(strings.categoryOther, Icons.Filled.MoreHoriz, "other")
    )


    val customCategoryList = vm.customCategories.toList()

    // ★ 根據目前的 type 切換顯示的列表
    val displayCategories = remember(customCategoryList, strings, type) {
        // 使用 "Expense" 是為了相容性，但 UI 主要操作是 "支出"
        val baseList = if (type == "支出" || type == "Expense") expenseCategories else incomeCategories
        val customItems = customCategoryList.map {
            CategoryItem(it.name, Icons.Filled.Label, it.key)
        }
        // 在列表最後加上「新增」按鈕
        baseList + customItems + CategoryItem(strings.categoryAdd, Icons.Filled.Add, "add")
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dateMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { dateMillis = it }
                    showDatePicker = false
                }) { Text(strings.btnConfirm) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text(strings.btnCancel) }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // 新增類別的 Dialog
    if (showNewCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showNewCategoryDialog = false },
            title = { Text(strings.categoryAdd) },
            text = {
                Column {
                    Text("請輸入類別名稱", modifier = Modifier.padding(bottom = 8.dp))
                    OutlinedTextField(
                        value = newCategoryName,
                        onValueChange = { newCategoryName = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newCategoryName.isNotBlank()) {
                        val newKey = vm.addCustomCategory(newCategoryName)
                        selectedCategoryKey = newKey
                        newCategoryName = ""
                        showNewCategoryDialog = false
                    }
                }) { Text(strings.btnConfirm) }
            },
            dismissButton = {
                TextButton(onClick = { showNewCategoryDialog = false }) { Text(strings.btnCancel) }
            }
        )
    }

    Scaffold(
        containerColor = BgColor,
        topBar = {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(BgColor)
                    .statusBarsPadding()
                    .padding(vertical = 12.dp, horizontal = 4.dp),
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = TextColor)
                }

                Box(Modifier.align(Alignment.Center)) {
                    // ★ 切換 Type 時，預設選取列表的第一個項目
                    TypeSegmentedControl(type, strings) { newType ->
                        type = newType
                        // 切換時重置選擇，避免 Key 不存在
                        if (newType == "支出" || newType == "Expense") {
                            selectedCategoryKey = ""
                        } else {
                            selectedCategoryKey = ""
                        }
                    }
                }

                if (isEditMode) {
                    IconButton(
                        onClick = {
                            vm.deleteTransaction(transactionId)
                            onBack()
                        },
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(Icons.Default.Delete, null, tint = Color(0xFFE57373))
                    }
                }
            }
        }
    ) { pad ->
        Column(
            Modifier
                .padding(pad)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(displayCategories) { item ->
                    CategoryCell(
                        item = item,
                        isSelected = selectedCategoryKey == item.key,
                        onClick = {
                            if (item.key == "add") {
                                showNewCategoryDialog = true
                            } else {
                                selectedCategoryKey = item.key
                            }
                        }
                    )
                }
            }

            Column(
                Modifier
                    .fillMaxWidth()
                    .background(BgColor)
                    .padding(horizontal = 18.dp, vertical = 10.dp)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Edit, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(10.dp))

                    Box(Modifier.weight(1f)) {
                        if (note.isEmpty()) Text(strings.inputNote, color = Color.LightGray, fontSize = 16.sp)
                        BasicTextField(
                            value = note,
                            onValueChange = { note = it },
                            textStyle = TextStyle(fontSize = 16.sp, color = TextColor),
                            singleLine = true
                        )
                    }

                    Text("$currency $amount", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextColor)
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .padding(horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { addDays(-1) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = TextColor)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { showDatePicker = true }
                            .padding(8.dp)
                    ) {
                        Icon(Icons.Filled.DateRange, null, modifier = Modifier.size(16.dp), tint = TextColor)
                        Spacer(Modifier.width(8.dp))
                        Text(formatDate(dateMillis), color = TextColor, fontWeight = FontWeight.Medium)
                    }

                    IconButton(onClick = { addDays(1) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = TextColor)
                    }
                }
            }

            CalculatorSection(
                amount = amount,
                onInput = { amount = it },
                onDone = onBack,
                note = note,
                handleAction = { finalAmount ->
                    val title = if (note.isNotEmpty()) note else ""

                    if (isEditMode) {
                        vm.updateTransaction(transactionId, title, finalAmount, type, dateMillis, selectedCategoryKey)
                    } else {
                        vm.addTransaction(title, finalAmount, type, dateMillis, selectedCategoryKey)
                    }
                    onBack()
                },
                btnDoneText = if (isEditMode) strings.btnConfirm else strings.btnDone
            )
        }
    }
}

@Composable
fun TypeSegmentedControl(current: String, strings: com.example.accountbook.viewmodel.StringResources, onSelection: (String) -> Unit) {
    val items = listOf(strings.tabExpense, strings.tabIncome)

    Row(
        Modifier
            .border(1.dp, Color(0xFFD0CDC5), RoundedCornerShape(50))
            .background(Color(0xFFFEFDFD), RoundedCornerShape(50))
            .height(34.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { text ->
            val isSelected = current == text
            Box(
                Modifier
                    .clickable { onSelection(text) }
                    .background(
                        if (isSelected) TextColor else Color(0xFFFEFDFD),
                        RoundedCornerShape(50)
                    )
                    .padding(horizontal = 26.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text,
                    color = if (isSelected) Color.White else TextColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun CategoryCell(item: CategoryItem, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            Modifier
                .size(60.dp)
                .border(1.dp, if(isSelected) Color(0xFFC9BEA8) else BorderColor, RoundedCornerShape(18.dp))
                .background(
                    if (isSelected) AccentYellow else Color.White,
                    RoundedCornerShape(18.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.name,
                tint = TextColor,
                modifier = Modifier.size(30.dp)
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(item.name, fontSize = 13.sp, color = TextColor, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun CalculatorSection(
    amount: String,
    onInput: (String) -> Unit,
    onDone: () -> Unit,
    note: String,
    handleAction: (Int) -> Unit,
    btnDoneText: String
) {
    val btnShape = RoundedCornerShape(10.dp)
    val btnColor = Color(0xFFF3EFE8)

    fun calculateExpression(expression: String): String {
        val exp = expression.replace("x", "*")
        val operators = charArrayOf('+', '-', '*', '/')
        val opIndex = exp.indexOfAny(operators, startIndex = 1)
        if (opIndex == -1) return expression
        if (opIndex == exp.lastIndex) return expression.dropLast(1)
        try {
            val op = exp[opIndex]
            val num1 = exp.substring(0, opIndex).toDouble()
            val num2 = exp.substring(opIndex + 1).toDouble()
            val result = when (op) {
                '+' -> num1 + num2
                '-' -> num1 - num2
                '*' -> num1 * num2
                '/' -> if (num2 != 0.0) num1 / num2 else 0.0
                else -> 0.0
            }
            return if (result % 1.0 == 0.0) result.toInt().toString() else String.format("%.2f", result).trimEnd('0').trimEnd('.')
        } catch (e: Exception) { return expression }
    }

    fun handleInput(key: String) {
        val operators = listOf("+", "-", "x", "/")
        when {
            key == "AC" -> onInput("0")
            key == "BS" -> onInput(if (amount.length > 1) amount.dropLast(1) else "0")
            operators.contains(key) -> {
                if (operators.any { amount.endsWith(it) }) {
                    onInput(amount.dropLast(1) + key)
                } else if (amount.any { it in "+-x/" && it != amount.first() }) {
                    val result = calculateExpression(amount)
                    onInput(result + key)
                } else {
                    onInput(amount + key)
                }
            }
            key == "." -> {
                val lastPart = amount.split(*"+-x/".toCharArray()).last()
                if (!lastPart.contains(".")) onInput(amount + ".")
            }
            else -> if (amount == "0") onInput(key) else onInput(amount + key)
        }
    }

    fun handleDoneClick() {
        if (amount.any { it in "+-x/" && it != amount.first() }) {
            val result = calculateExpression(amount)
            onInput(result)
        } else {
            val finalAmount = amount.toIntOrNull() ?: 0
            if (finalAmount > 0) {
                handleAction(finalAmount)
            } else {
                onDone()
            }
        }
    }

    val showEquals = amount.any { it in "+-x/" && it != amount.first() }

    @Composable
    fun CalBtn(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
        Box(
            modifier = modifier
                .fillMaxHeight()
                .clip(btnShape)
                .background(btnColor)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(text, fontSize = 22.sp, fontWeight = FontWeight.SemiBold, color = TextColor)
        }
    }

    Column(
        Modifier
            .fillMaxWidth()
            .background(BgColor)
            .padding(12.dp)
            .height(280.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            CalBtn("7", Modifier.weight(1f)) { handleInput("7") }
            CalBtn("8", Modifier.weight(1f)) { handleInput("8") }
            CalBtn("9", Modifier.weight(1f)) { handleInput("9") }
            CalBtn("/", Modifier.weight(1f)) { handleInput("/") }
            CalBtn("AC", Modifier.weight(1f)) { handleInput("AC") }
        }
        Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            CalBtn("4", Modifier.weight(1f)) { handleInput("4") }
            CalBtn("5", Modifier.weight(1f)) { handleInput("5") }
            CalBtn("6", Modifier.weight(1f)) { handleInput("6") }
            CalBtn("x", Modifier.weight(1f)) { handleInput("x") }
            Box(
                Modifier.weight(1f).fillMaxHeight().clip(btnShape).background(btnColor)
                    .clickable { handleInput("BS") },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = TextColor)
            }
        }
        Row(Modifier.weight(2f), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Column(Modifier.weight(4f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    CalBtn("1", Modifier.weight(1f)) { handleInput("1") }
                    CalBtn("2", Modifier.weight(1f)) { handleInput("2") }
                    CalBtn("3", Modifier.weight(1f)) { handleInput("3") }
                    CalBtn("+", Modifier.weight(1f)) { handleInput("+") }
                }
                Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    CalBtn("00", Modifier.weight(1f)) { handleInput("00") }
                    CalBtn("0", Modifier.weight(1f)) { handleInput("0") }
                    CalBtn(".", Modifier.weight(1f)) { handleInput(".") }
                    CalBtn("-", Modifier.weight(1f)) { handleInput("-") }
                }
            }
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(btnShape)
                    .background(PinkButtonColor)
                    .clickable { handleDoneClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (showEquals) "=" else btnDoneText,
                    fontSize = if (showEquals) 28.sp else 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextColor
                )
            }
        }
    }
}