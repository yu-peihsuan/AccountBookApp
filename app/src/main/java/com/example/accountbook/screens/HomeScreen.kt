@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.accountbook.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.accountbook.viewmodel.TransactionViewModel
import com.example.accountbook.viewmodel.Transaction
import com.example.accountbook.viewmodel.StringResources
import java.util.Calendar

val TopBarColor = Color(0xFFFCF5E8)
val AppBgColor = Color(0xFFFDFBF7)
val CardBorderColor = Color(0xFFEBEBEB)
val CardYellow = Color(0xFFEDD86D)
val CardBlue = Color(0xFF7CB9E8)
val TextGray = Color(0xFF4A463F)

@Composable
fun HomeScreen(vm: TransactionViewModel, nav: NavHostController, onOpenDrawer: () -> Unit) {

    val strings = vm.currentStrings
    val calendar = remember { Calendar.getInstance() }
    var currentYear by remember { mutableIntStateOf(calendar.get(Calendar.YEAR)) }
    var currentMonth by remember { mutableIntStateOf(calendar.get(Calendar.MONTH) + 1) }

    var showMonthPicker by remember { mutableStateOf(false) }

    val allTransactions = vm.transactions
    val filteredData = remember(allTransactions, currentYear, currentMonth) {
        allTransactions.filter { tx ->
            val parts = tx.date.split("/")
            if (parts.size >= 2) {
                val y = parts[0].toIntOrNull()
                val m = parts[1].toIntOrNull()
                y == currentYear && m == currentMonth
            } else false
        }
    }

    val budget = vm.budget
    val currency = vm.currency

    fun isExpense(t:String) = t=="支出"||t=="Expense"
    fun isIncome(t:String) = t=="收入"||t=="Income"

    val expense = filteredData.filter { isExpense(it.type) }.sumOf { it.amount }
    val income = filteredData.filter { isIncome(it.type) }.sumOf { it.amount }
    val remainBudget = budget - expense
    val monthBalance = income - expense
    val percent = if (budget > 0) ((remainBudget.toFloat() / budget) * 100).toInt().coerceIn(0, 100) else 0

    val list = remember(filteredData) {
        filteredData.groupBy { "${it.date} ${it.day}" }
            .map { (dateKey, txList) ->
                val dayIncome = txList.filter { isIncome(it.type) }.sumOf { it.amount }
                val dayExpense = txList.filter { isExpense(it.type) }.sumOf { it.amount }
                val dayTotal = dayIncome - dayExpense
                Triple(dateKey, txList, dayTotal)
            }
            .sortedByDescending { it.first }
    }

    if (showMonthPicker) {
        MonthPickerDialog(
            year = currentYear,
            month = currentMonth,
            onDismiss = { showMonthPicker = false },
            onConfirm = { y, m ->
                currentYear = y
                currentMonth = m
                showMonthPicker = false
            }
        )
    }

    Scaffold(
        containerColor = AppBgColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showMonthPicker = true }
                            .padding(8.dp)
                    ) {
                        Icon(Icons.Default.DateRange, null, Modifier.size(18.dp), tint = TextGray)
                        Spacer(Modifier.width(8.dp))
                        Text("${currentYear}/${currentMonth}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextGray)
                        Icon(Icons.Default.ArrowDropDown, null, tint = TextGray)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, null, tint = TextGray)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = TopBarColor
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { nav.navigate("add") }, // 新增時不帶 ID，預設為 -1
                containerColor = Color(0xFFF2EAD5),
                shape = RoundedCornerShape(50),
                modifier = Modifier.size(width = 110.dp, height = 48.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, null)
                    Spacer(Modifier.width(4.dp))
                    Text(strings.menuAdd, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { pad ->

        LazyColumn(
            Modifier
                .padding(pad)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { BudgetCard(percent, budget, remainBudget, currency, strings) }

            item {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, CardBorderColor, RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val sign = if (monthBalance < 0) "-" else ""
                    Text("${strings.balance} : $sign $currency${Math.abs(monthBalance)}", fontSize = 18.sp, color = Color.Gray)
                }
            }

            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    InfoCard(title = strings.tabExpense, money = "$currency$expense", color = CardYellow, modifier = Modifier.weight(1f))
                    InfoCard(title = strings.tabIncome, money = "$currency$income", color = CardBlue, modifier = Modifier.weight(1f))
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE0E0E0))
                    Text("  ${strings.historyTitle}  ", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE0E0E0))
                }
            }

            items(list) { (date, transactions, dayTotal) ->
                DayRecord(
                    date,
                    transactions,
                    dayTotal,
                    currency,
                    strings,
                    vm,
                    onItemClick = { tx ->
                        // 點擊項目時，導航至編輯頁面並帶上該筆資料的 ID
                        nav.navigate("add?id=${tx.id}")
                    }
                )
            }

            item { Spacer(Modifier.height(60.dp)) }
        }
    }
}

@Composable
fun InfoCard(title: String, money: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier
            .height(85.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(color)
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()) {
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color.White)
            Spacer(Modifier.height(4.dp))
            Text(money, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun DayRecord(
    date: String,
    transactions: List<Transaction>,
    dayTotal: Int,
    currency: String,
    strings: StringResources,
    vm: TransactionViewModel,
    onItemClick: (Transaction) -> Unit // 新增點擊 callback
) {
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, CardBorderColor, RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(bottom = 0.dp)
    ) {
        Column {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(date, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextGray)

                when {
                    dayTotal < 0 -> Text("-$currency${Math.abs(dayTotal)}", fontSize = 20.sp, color = Color(0xFFE57373), fontWeight = FontWeight.Bold)
                    dayTotal > 0 -> Text("$currency${dayTotal}", fontSize = 20.sp, color = CardBlue, fontWeight = FontWeight.Bold)
                    else -> Text("$currency${dayTotal}", fontSize = 20.sp, color = TextGray, fontWeight = FontWeight.Bold)
                }
            }
            HorizontalDivider(color = Color(0xFFF5F5F5), thickness = 1.dp)

            transactions.forEachIndexed { index, tx ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable { onItemClick(tx) } // 加入點擊觸發
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val iconKey = if (tx.categoryKey.isNotEmpty()) tx.categoryKey else tx.title
                    Icon(getIconByKey(iconKey), null, tint = TextGray, modifier = Modifier.size(24.dp))

                    Spacer(Modifier.width(16.dp))

                    val displayText = if (tx.title.isNotEmpty()) tx.title else vm.getCategoryName(tx.categoryKey)

                    Text(displayText, fontSize = 20.sp, color = TextGray, modifier = Modifier.weight(1f))

                    val isExp = tx.type == "支出" || tx.type == "Expense"

                    if (isExp) {
                        Text("-$currency${tx.amount}", fontSize = 20.sp, color = TextGray)
                    } else {
                        Text("$currency${tx.amount}", fontSize = 20.sp, color = CardBlue)
                    }
                }

                if (index != transactions.lastIndex)
                    HorizontalDivider(color = Color(0xFFFAFAFA), thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}

fun getIconByKey(key: String): ImageVector {
    return when {
        key.contains("traffic") || key.contains("交通") -> Icons.Default.Place
        key.contains("lunch") || key.contains("dinner") || key.contains("breakfast") || key.contains("食") -> Icons.Default.Star
        key.contains("drink") || key.contains("飲") -> Icons.Default.Face
        key.contains("shopping") || key.contains("購") -> Icons.Default.ShoppingCart
        key.contains("bills") || key.contains("電") -> Icons.Default.Phone
        key.contains("rent") || key.contains("房") -> Icons.Default.Home
        else -> Icons.Default.Home
    }
}

@Composable
fun BudgetCard(percent:Int, budget:Int, remain:Int, currency: String, strings: StringResources) {
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, CardBorderColor, RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(90.dp)) {
                Canvas(Modifier.size(90.dp)) {
                    drawArc(
                        color = Color(0xFFF0F0F0),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 25f)
                    )
                    drawArc(
                        color = Color(0xFF7EC366),
                        startAngle = -90f,
                        sweepAngle = percent * 3.6f,
                        useCenter = false,
                        style = Stroke(width = 25f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                    )
                }
                Text("${percent}%", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextGray)
            }
            Spacer(Modifier.width(28.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(strings.budget, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextGray)
                    Spacer(Modifier.width(24.dp))
                    Text("$currency${budget}", fontSize = 20.sp, color = Color.Gray)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(strings.remain, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextGray)
                    Spacer(Modifier.width(24.dp))
                    Box(
                        Modifier
                            .background(Color(0xFF7EC366), RoundedCornerShape(50))
                            .padding(horizontal = 10.dp, vertical = 2.dp)
                    ) {
                        Text("$currency${remain}", fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}