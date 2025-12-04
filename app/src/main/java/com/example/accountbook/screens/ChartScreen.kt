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
val TopBarBgColor = Color(0xFFFCF5E8) // ★ 上方米色
val BodyBgColor = Color.White         // ★ 下方白色
val ChartYellow = Color(0xFFEAC45D)   // 圖表黃 (稍微深一點以符合圖片)
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
    // 監聽年份、月份與 Tab 變化，重新載入資料
    LaunchedEffect(vm.chartYear, vm.chartMonth, vm.chartTab) {
        vm.loadMonthlyChartData()
    }

    Scaffold(
        containerColor = BodyBgColor, // ★ 修改：整體背景改為白色
        topBar = {
            // ★ 修改：只有這一塊是米色
            Column(Modifier.background(TopBarBgColor)) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 16.dp) // 增加一點底部 padding 讓米色區塊顯眼
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
                .background(BodyBgColor), // 確保內容區是白色
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(16.dp))

            // 1. 時間篩選器 (移到這裡，背景為白)
            TimeFilterSection(vm)

            Spacer(Modifier.height(24.dp))

            // 內容區塊加上 padding
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 2. 每日趨勢圖卡片
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
                .width(300.dp) // ★ 設定固定寬度讓它像圖片一樣長
                .border(1.dp, Color.LightGray, RoundedCornerShape(50))
                .clip(RoundedCornerShape(50))
                .background(Color.White)
                .padding(2.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val filters = listOf("月", "年", "自訂")
            var selectedFilter by remember { mutableIntStateOf(0) } // 0=月 (預設)

            filters.forEachIndexed { index, text ->
                val isSelected = selectedFilter == index
                Box(
                    Modifier
                        .weight(1f) // 平均分配寬度
                        .clickable { selectedFilter = index }
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

        // (B) 日期切換 ( < 2025年 11月 > )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween // 讓箭頭分開一點，或者用 Center 也可以
        ) {
            // 為了讓中間文字置中，我們可以用 Box 或 Weight
            Box(Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                IconButton(onClick = {
                    if (vm.chartMonth == 1) {
                        vm.chartMonth = 12
                        vm.chartYear--
                    } else {
                        vm.chartMonth--
                    }
                }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null, tint = TextDark)
                }
            }

            Text(
                "${vm.chartYear}年 ${vm.chartMonth}月",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                IconButton(onClick = {
                    if (vm.chartMonth == 12) {
                        vm.chartMonth = 1
                        vm.chartYear++
                    } else {
                        vm.chartMonth++
                    }
                }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = TextDark)
                }
            }
        }
    }
}

// --- 元件: 每日趨勢圖卡片 ---
@Composable
fun DailyTrendCard(vm: TransactionViewModel) {
    Column {
        // ★ 標題列：Icon + 文字
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(Modifier.width(8.dp))
            Text("每日趨勢", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
        }

        Spacer(Modifier.height(12.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderGray),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp) // 稍微加高一點
        ) {
            Column(Modifier.padding(16.dp)) {
                // 圖例 (Legend)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(12.dp, 3.dp).background(DashBlue))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "平均每日 : ${if(vm.chartTab==0) "-" else ""}${vm.currency}${vm.averageDaily}",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }

                Spacer(Modifier.height(20.dp))

                // 長條圖 Canvas
                val data = vm.monthlyDailyStats
                val daysInMonth = 31

                Canvas(Modifier.fillMaxSize()) {
                    val barWidth = 6.dp.toPx() // 稍微調細一點，符合圖片
                    val spacing = (size.width - (daysInMonth * barWidth)) / (daysInMonth - 1)
                    val maxVal = (data.values.maxOrNull() ?: 1).coerceAtLeast(vm.averageDaily * 2).toFloat()
                    val chartHeight = size.height - 40f // 預留底部文字空間

                    // 1. 畫平均線 (藍色虛線)
                    val avgY = chartHeight - (vm.averageDaily / maxVal * chartHeight)
                    drawLine(
                        color = DashBlue,
                        start = Offset(0f, avgY),
                        end = Offset(size.width, avgY),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f)), // 虛線間距大一點
                        strokeWidth = 3f
                    )

                    // 2. 畫底部黃色基準線 (★ 新增)
                    drawLine(
                        color = ChartYellow,
                        start = Offset(0f, chartHeight),
                        end = Offset(size.width, chartHeight),
                        strokeWidth = 3f
                    )

                    // 3. 畫長條
                    for (i in 1..daysInMonth) {
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

                        // 4. 畫 X 軸標籤 (1, 3, 5...)
                        if (i % 2 != 0) { // 只畫奇數
                            val textPaint = android.graphics.Paint().apply {
                                color = android.graphics.Color.LTGRAY
                                textSize = 30f
                                textAlign = android.graphics.Paint.Align.LEFT
                            }
                            // 修正文字位置置中
                            val textX = x - 5f
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

// --- 元件: 交易類型 (甜甜圈圖) ---
@Composable
fun TypeAnalysisCard(vm: TransactionViewModel) {
    val stats = vm.monthlyCategoryStats
    val colors = listOf(ChartBlue, ChartGreen, ChartPink, ChartYellow, Color(0xFFBA68C8), Color(0xFFFF8A65))
    val total = vm.monthlyTotal

    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // 這裡也可以加 Icon，但圖片沒顯示，保持原樣
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