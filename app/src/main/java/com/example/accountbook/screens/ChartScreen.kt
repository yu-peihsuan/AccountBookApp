@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.accountbook.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.accountbook.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Calendar

// 定義顏色
val TopBarBgColor = Color(0xFFFCF5E8)
val BodyBgColor = Color.White
val ChartYellow = Color(0xFFEAC45D)
val ChartBlue = Color(0xFF7CB9E8)
val ChartGreen = Color(0xFF81C784)
val ChartPink = Color(0xFFE57373)
val TextDark = Color(0xFF4A463F)
val BorderGray = Color(0xFFE0E0E0)
val DashBlue = Color(0xFF7CB9E8)

@Composable
fun ChartScreen(
    vm: TransactionViewModel,
    onBack: () -> Unit,
    onOpenDrawer: () -> Unit
) {
    // 監聽年份、月份、Tab、模式、自訂日期變化，重新載入資料
    LaunchedEffect(vm.chartYear, vm.chartMonth, vm.chartTab, vm.chartTimeMode, vm.customStartDateMillis, vm.customEndDateMillis) {
        vm.loadMonthlyChartData()
    }

    Scaffold(
        containerColor = BodyBgColor,
        topBar = {
            Column(Modifier.background(TopBarBgColor)) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 16.dp)
                ) {
                    // 左側 Menu Icon
                    IconButton(
                        onClick = onOpenDrawer,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 8.dp)
                    ) {
                        Icon(Icons.Default.Menu, null, tint = TextDark)
                    }

                    // 中間 Segmented Toggle (支出 / 收入 / 結餘)
                    Row(
                        Modifier
                            .align(Alignment.Center)
                            .border(1.dp, TextDark, RoundedCornerShape(50))
                            .clip(RoundedCornerShape(50))
                            .background(Color.White)
                    ) {
                        val tabs = listOf("支出", "收入", "結餘")
                        tabs.forEachIndexed { index, text ->
                            val isSelected = vm.chartTab == index
                            Box(
                                Modifier
                                    .clickable { vm.chartTab = index }
                                    .background(Color.Transparent)
                                    .padding(horizontal = 24.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text,
                                    color = if (isSelected) TextDark else Color.Gray,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(BodyBgColor),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(16.dp))

            // 1. 時間篩選器
            TimeFilterSection(vm)

            Spacer(Modifier.height(24.dp))

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 2. 趨勢圖卡片
                DailyTrendCard(vm)

                // 3. 交易類型 (甜甜圈圖 + 圖例)
                TypeAnalysisCard(vm)

                // 4. 明細列表
                DetailListCard(vm)

                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

// --- 元件: 時間篩選區塊 ---
@Composable
fun TimeFilterSection(vm: TransactionViewModel) {
    var showMonthPicker by remember { mutableStateOf(false) }

    // 控制日期選擇器的顯示 (0:無, 1:起始日期, 2:結束日期)
    var datePickerState by remember { mutableIntStateOf(0) }

    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // (A) 月 / 年 / 自訂 膠囊按鈕
        Row(
            Modifier
                .width(300.dp)
                .border(1.dp, Color.LightGray, RoundedCornerShape(50))
                .clip(RoundedCornerShape(50))
                .background(Color.White)
                .padding(2.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val filters = listOf("月", "年", "自訂")

            filters.forEachIndexed { index, text ->
                val isSelected = vm.chartTimeMode == index
                Box(
                    Modifier
                        .weight(1f)
                        .clickable { vm.chartTimeMode = index }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text,
                        color = if (isSelected) TextDark else Color.LightGray,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // (B) 日期顯示區
        if (vm.chartTimeMode == 2) {
            // === 自訂模式：顯示兩個日期框與快速按鈕 ===
            val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            val startStr = sdf.format(Date(vm.customStartDateMillis))
            val endStr = sdf.format(Date(vm.customEndDateMillis))

            Row(
                Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                DateBox(dateStr = startStr, onClick = { datePickerState = 1 })
                Text("~", fontSize = 20.sp, color = TextDark, fontWeight = FontWeight.Bold)
                DateBox(dateStr = endStr, onClick = { datePickerState = 2 })
            }

            Spacer(Modifier.height(12.dp))

            // 快速選擇按鈕
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                QuickChip("近7天") { vm.applyQuickRange("7days") }
                QuickChip("近30天") { vm.applyQuickRange("30days") }
                QuickChip("本月") { vm.applyQuickRange("thisMonth") }
            }

        } else {
            // === 月/年模式：顯示左右箭頭與日期 ===
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 左箭頭
                Box(Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                    IconButton(onClick = {
                        if (vm.chartTimeMode == 1) {
                            vm.chartYear--
                        } else {
                            if (vm.chartMonth == 1) {
                                vm.chartMonth = 12
                                vm.chartYear--
                            } else {
                                vm.chartMonth--
                            }
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null, tint = TextDark)
                    }
                }

                // 中間日期文字 (可點擊開啟滾動式選擇器)
                val displayDate = if (vm.chartTimeMode == 1) {
                    "${vm.chartYear}年"
                } else {
                    "${vm.chartYear}年 ${vm.chartMonth}月"
                }

                Box(
                    Modifier
                        .clickable { showMonthPicker = true }
                        .padding(8.dp)
                ) {
                    Text(
                        displayDate,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }

                // 右箭頭
                Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                    IconButton(onClick = {
                        if (vm.chartTimeMode == 1) {
                            vm.chartYear++
                        } else {
                            if (vm.chartMonth == 12) {
                                vm.chartMonth = 1
                                vm.chartYear++
                            } else {
                                vm.chartMonth++
                            }
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = TextDark)
                    }
                }
            }
        }
    }

    // 滾動式月份選擇器 Dialog
    if (showMonthPicker) {
        MonthPickerDialog(
            year = vm.chartYear,
            month = vm.chartMonth,
            showMonth = (vm.chartTimeMode == 0), // ★ 如果是年模式(1)，不顯示月份
            onDismiss = { showMonthPicker = false },
            onConfirm = { y, m ->
                vm.chartYear = y
                vm.chartMonth = m
                showMonthPicker = false
            }
        )
    }

    // 單一日期選擇器 (用於自訂起始與結束日期)
    if (datePickerState != 0) {
        val initialDate = if (datePickerState == 1) vm.customStartDateMillis else vm.customEndDateMillis
        val dateState = rememberDatePickerState(initialSelectedDateMillis = initialDate)

        DatePickerDialog(
            onDismissRequest = { datePickerState = 0 },
            confirmButton = {
                TextButton(onClick = {
                    dateState.selectedDateMillis?.let {
                        if (datePickerState == 1) {
                            vm.customStartDateMillis = it
                            if (vm.customStartDateMillis > vm.customEndDateMillis) {
                                vm.customEndDateMillis = vm.customStartDateMillis
                            }
                        } else {
                            vm.customEndDateMillis = it
                            if (vm.customEndDateMillis < vm.customStartDateMillis) {
                                vm.customStartDateMillis = vm.customEndDateMillis
                            }
                        }
                    }
                    datePickerState = 0
                }) { Text("確定") }
            },
            dismissButton = {
                TextButton(onClick = { datePickerState = 0 }) { Text("取消") }
            }
        ) {
            DatePicker(state = dateState)
        }
    }
}

@Composable
fun DateBox(dateStr: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .background(Color.White)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.DateRange, null, tint = TextDark, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(8.dp))
            Text(dateStr, fontSize = 16.sp, color = TextDark)
        }
    }
}

@Composable
fun QuickChip(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(50))
            .clip(RoundedCornerShape(50))
            .background(Color(0xFFF9F9F9))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(text, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun DailyTrendCard(vm: TransactionViewModel) {
    val isYearMode = vm.chartTimeMode == 1
    val isCustomMode = vm.chartTimeMode == 2

    val title = when {
        isYearMode -> "每月趨勢"
        isCustomMode -> "區間趨勢"
        else -> "每日趨勢"
    }

    val avgLabel = when {
        isYearMode -> "平均每月"
        else -> "平均每日"
    }

    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(Modifier.width(8.dp))
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
        }

        Spacer(Modifier.height(12.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderGray),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(12.dp, 3.dp).background(DashBlue))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "$avgLabel : ${if(vm.chartTab==0) "-" else ""}${vm.currency}${vm.averageDaily}",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }

                Spacer(Modifier.height(20.dp))

                val dataPoints = vm.chartDataPoints
                val count = dataPoints.size

                if (count == 0) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("無資料", color = Color.Gray)
                    }
                } else {
                    Canvas(Modifier.fillMaxSize()) {
                        val chartWidth = size.width
                        val chartHeight = size.height - 40f

                        val spacing = if (count > 1) chartWidth / (count) else 0f
                        val barWidth = (spacing * 0.6f).coerceAtMost(30.dp.toPx()).coerceAtLeast(2.dp.toPx())

                        val maxVal = (dataPoints.maxOfOrNull { it.second } ?: 1).coerceAtLeast(vm.averageDaily * 2).toFloat()

                        val avgY = chartHeight - (vm.averageDaily / maxVal * chartHeight)
                        if (avgY >= 0) {
                            drawLine(
                                color = DashBlue,
                                start = Offset(0f, avgY),
                                end = Offset(size.width, avgY),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f)),
                                strokeWidth = 3f
                            )
                        }

                        drawLine(
                            color = ChartYellow,
                            start = Offset(0f, chartHeight),
                            end = Offset(size.width, chartHeight),
                            strokeWidth = 3f
                        )

                        dataPoints.forEachIndexed { i, (label, amount) ->
                            val cx = i * spacing + spacing / 2
                            val x = cx - barWidth / 2

                            if (amount > 0) {
                                val barHeight = (amount / maxVal) * chartHeight
                                val y = chartHeight - barHeight

                                drawRect(
                                    color = ChartYellow,
                                    topLeft = Offset(x, y),
                                    size = Size(barWidth, barHeight)
                                )
                            }

                            val step = when {
                                count <= 12 -> 1
                                count <= 31 -> 2
                                else -> count / 10 + 1
                            }

                            if (i % step == 0) {
                                val textPaint = android.graphics.Paint().apply {
                                    color = android.graphics.Color.LTGRAY
                                    textSize = 30f
                                    textAlign = android.graphics.Paint.Align.CENTER
                                }
                                drawContext.canvas.nativeCanvas.drawText(
                                    label, cx, size.height, textPaint
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TypeAnalysisCard(vm: TransactionViewModel) {
    val stats = vm.monthlyCategoryStats
    val colors = listOf(ChartBlue, ChartGreen, ChartPink, ChartYellow, Color(0xFFBA68C8), Color(0xFFFF8A65))
    val total = vm.monthlyTotal

    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("  交易類型", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
        }

        Spacer(Modifier.height(8.dp))

        Box(
            Modifier
                .fillMaxWidth()
                .height(260.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(Modifier.size(180.dp)) {
                var startAngle = -90f
                val strokeWidth = 50.dp.toPx()

                if (stats.isEmpty()) {
                    drawCircle(Color.LightGray, style = Stroke(strokeWidth))
                } else {
                    stats.forEachIndexed { index, stat ->
                        val sweep = (stat.totalAmount.toFloat() / total) * 360f
                        drawArc(
                            color = colors[index % colors.size],
                            startAngle = startAngle,
                            sweepAngle = sweep,
                            useCenter = false,
                            style = Stroke(strokeWidth, cap = StrokeCap.Butt)
                        )
                        startAngle += sweep
                    }
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(if (vm.chartTab == 1) "總收入" else "總支出", fontSize = 12.sp, color = Color.Gray)
                Text("${vm.currency}$total", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderGray),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp)) {
                stats.chunked(2).forEachIndexed { i, rowItems ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        rowItems.forEach { item ->
                            val colorIndex = stats.indexOf(item) % colors.size
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 8.dp)
                            ) {
                                Box(Modifier.size(12.dp).background(colors[colorIndex]))
                                Spacer(Modifier.width(12.dp))
                                Text(vm.getCategoryName(item.key), fontSize = 14.sp, color = TextDark)
                                Text(
                                    String.format(Locale.getDefault(), " %.1f%%", item.percentage),
                                    fontSize = 14.sp,
                                    color = TextDark
                                )
                                Spacer(Modifier.width(16.dp))
                            }
                        }
                        if (rowItems.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun DetailListCard(vm: TransactionViewModel) {
    val stats = vm.monthlyCategoryStats
    val colors = listOf(ChartBlue, ChartGreen, ChartPink, ChartYellow, Color(0xFFBA68C8), Color(0xFFFF8A65))
    val isExpense = vm.chartTab == 0

    Column {
        Text(if(isExpense) "  支出明細" else "  收入明細", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
        Spacer(Modifier.height(8.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderGray),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                stats.forEachIndexed { index, item ->
                    val color = colors[index % colors.size]

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(Modifier.size(16.dp).background(color))
                        Spacer(Modifier.width(16.dp))
                        Text(vm.getCategoryName(item.key), fontSize = 16.sp, color = TextDark)
                        Spacer(Modifier.width(24.dp))
                        Text("(${item.count}筆)", fontSize = 12.sp, color = Color.Gray)
                        Spacer(Modifier.weight(1f))
                        val sign = if (isExpense) "-" else ""
                        Text(
                            "$sign${vm.currency}${item.totalAmount}",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }

                    if (index < stats.lastIndex) {
                        HorizontalDivider(
                            color = Color(0xFFF0F0F0),
                            thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}