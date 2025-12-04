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
import java.util.Locale

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
    // 監聽年份、月份、Tab 與模式變化，重新載入資料
    LaunchedEffect(vm.chartYear, vm.chartMonth, vm.chartTab, vm.chartTimeMode) {
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
                // 2. 每日/每月趨勢圖卡片
                DailyTrendCard(vm)

                // 3. 交易類型 (甜甜圈圖 + 圖例)
                TypeAnalysisCard(vm)

                // 4. 支出/收入明細列表
                DetailListCard(vm)

                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

// --- 元件: 時間篩選區塊 與 日期切換 ---
@Composable
fun TimeFilterSection(vm: TransactionViewModel) {
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

        // (B) 日期切換
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 左箭頭
            Box(Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                IconButton(onClick = {
                    if (vm.chartTimeMode == 1) {
                        // 年模式：減一年
                        vm.chartYear--
                    } else {
                        // 月模式：減一月
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

            // 中間日期文字
            val displayDate = if (vm.chartTimeMode == 1) {
                "${vm.chartYear}年"
            } else {
                "${vm.chartYear}年 ${vm.chartMonth}月"
            }

            Text(
                displayDate,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // 右箭頭
            Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                IconButton(onClick = {
                    if (vm.chartTimeMode == 1) {
                        // 年模式：加一年
                        vm.chartYear++
                    } else {
                        // 月模式：加一月
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

// --- 元件: 趨勢圖卡片 ---
@Composable
fun DailyTrendCard(vm: TransactionViewModel) {
    val isYearMode = vm.chartTimeMode == 1
    val title = if (isYearMode) "每月趨勢" else "每日趨勢"
    val avgLabel = if (isYearMode) "平均每月" else "平均每日"

    Column {
        // 標題列
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
                // 圖例
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

                // 長條圖 Canvas
                // 如果是年模式，顯示 12 個月；否則顯示 31 天
                val data = vm.monthlyDailyStats
                val count = if (isYearMode) 12 else 31

                Canvas(Modifier.fillMaxSize()) {
                    val barWidth = if (isYearMode) 12.dp.toPx() else 6.dp.toPx()
                    val spacing = (size.width - (count * barWidth)) / (count - 1)
                    val maxVal = (data.values.maxOrNull() ?: 1).coerceAtLeast(vm.averageDaily * 2).toFloat()
                    val chartHeight = size.height - 40f

                    // 1. 畫平均線 (藍色虛線)
                    val avgY = chartHeight - (vm.averageDaily / maxVal * chartHeight)
                    drawLine(
                        color = DashBlue,
                        start = Offset(0f, avgY),
                        end = Offset(size.width, avgY),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f)),
                        strokeWidth = 3f
                    )

                    // 2. 畫底部黃色基準線
                    drawLine(
                        color = ChartYellow,
                        start = Offset(0f, chartHeight),
                        end = Offset(size.width, chartHeight),
                        strokeWidth = 3f
                    )

                    // 3. 畫長條
                    for (i in 1..count) {
                        val amount = data[i] ?: 0
                        val x = (i - 1) * (barWidth + spacing)

                        if (amount > 0) {
                            val barHeight = (amount / maxVal) * chartHeight
                            val y = chartHeight - barHeight

                            drawRect(
                                color = ChartYellow,
                                topLeft = Offset(x, y),
                                size = Size(barWidth, barHeight)
                            )
                        }

                        // 4. 畫 X 軸標籤
                        // 年模式全畫 (1..12)，月模式只畫奇數 (1, 3, 5...)
                        if (isYearMode || i % 2 != 0) {
                            val textPaint = android.graphics.Paint().apply {
                                color = android.graphics.Color.LTGRAY
                                textSize = 30f
                                textAlign = android.graphics.Paint.Align.CENTER
                            }
                            val textX = x + barWidth / 2
                            drawContext.canvas.nativeCanvas.drawText(
                                "$i", textX, size.height, textPaint
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- 元件: 交易類型 ---
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

// --- 元件: 明細列表 ---
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