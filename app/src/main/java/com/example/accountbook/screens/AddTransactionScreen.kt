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

// ★ 修改：加入 key 屬性
data class CategoryItem(val name: String, val icon: ImageVector, val key: String)

@Composable
fun AddTransactionScreen(
    vm: TransactionViewModel,
    onBack: () -> Unit,
    // ★ 修改：onAdd 參數增加 categoryKey
    onAdd: (String, Int, String, Long, String) -> Unit
) {
    val strings = vm.currentStrings
    val currency = vm.currency
    val language = vm.language

    var type by remember { mutableStateOf(if(language=="English") "Expense" else "支出") }

    // ★ 修改：改為記錄選中的 Key (預設 rent)
    var selectedCategoryKey by remember { mutableStateOf("rent") }

    var amount by remember { mutableStateOf("0") }
    var note by remember { mutableStateOf("") }
    var dateMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }

    fun formatDate(millis: Long): String {
        val format = SimpleDateFormat("${strings.dateFormat} ${strings.dayFormat}",
            if(language=="English") Locale.US else Locale.TAIWAN)
        return format.format(Date(millis))
    }

    fun addDays(days: Int) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateMillis
        calendar.add(Calendar.DAY_OF_YEAR, days)
        dateMillis = calendar.timeInMillis
    }

    // ★ 修改：定義分類時加入 Key
    val categories = listOf(
        CategoryItem(strings.categoryBreakfast, Icons.Filled.Star, "breakfast"),
        CategoryItem(strings.categoryLunch, Icons.Filled.Face, "lunch"),
        CategoryItem(strings.categoryDinner, Icons.Filled.Favorite, "dinner"),
        CategoryItem(strings.categoryDrink, Icons.Filled.CheckCircle, "drink"),
        CategoryItem(strings.categoryTraffic, Icons.Filled.Share, "traffic"),
        CategoryItem(strings.categoryShopping, Icons.Filled.ShoppingCart, "shopping"),
        CategoryItem(strings.categoryDaily, Icons.Filled.ThumbUp, "daily"),
        CategoryItem(strings.categoryEntertainment, Icons.Filled.Face, "entertainment"),
        CategoryItem(strings.categoryRent, Icons.Filled.Home, "rent"),
        CategoryItem(strings.categoryBills, Icons.Filled.Warning, "bills"),
        CategoryItem(strings.categoryOther, Icons.Filled.Menu, "other"),
        CategoryItem(strings.categoryAdd, Icons.Filled.Add, "add")
    )

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
                    TypeSegmentedControl(type, strings) { type = it }
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
                items(categories) { item ->
                    CategoryCell(
                        item = item,
                        isSelected = selectedCategoryKey == item.key, // 比對 Key
                        onClick = { selectedCategoryKey = item.key }
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
                selectedCategoryKey = selectedCategoryKey, // 傳遞 Key
                type = type,
                dateMillis = dateMillis,
                onAdd = onAdd,
                btnDoneText = strings.btnDone
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
    selectedCategoryKey: String,
    type: String,
    dateMillis: Long,
    onAdd: (String, Int, String, Long, String) -> Unit,
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

    fun handleDone() {
        if (amount.any { it in "+-x/" && it != amount.first() }) {
            val result = calculateExpression(amount)
            onInput(result)
        } else {
            val finalAmount = amount.toIntOrNull() ?: 0
            if (finalAmount > 0) {
                // ★ 關鍵邏輯：
                // 如果有備註，title = note。
                // 如果沒備註，title = "" (空字串)，並傳送 selectedCategoryKey。
                // 這樣 HomeScreen 就可以知道：空字串 -> 用 Key 翻譯顯示；非空 -> 顯示使用者自訂文字。
                val title = if (note.isNotEmpty()) note else ""
                onAdd(title, finalAmount, type, dateMillis, selectedCategoryKey)
            }
            onDone()
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
                    .clickable { handleDone() },
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