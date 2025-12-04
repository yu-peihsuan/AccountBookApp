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
import androidx.compose.material.icons.filled.Info // ★ 修正：加入 Info import
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
import androidx.compose.ui.graphics.graphicsLayer // ★ 修正：加入 graphicsLayer import
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.accountbook.viewmodel.TransactionViewModel
import java.util.Locale

// 定義顏色
val BeigeBg = Color(0xFFFDFBF7)
val ChartYellow = Color(0xFFECD565)
val ChartBlue = Color(0xFF7CB9E8)
val ChartGreen = Color(0xFF81C784)
val ChartPink = Color(0xFFE57373)
val TextDark = Color(0xFF4A463F)
val BorderGray = Color(0xFFE0E0E0)
val DashBlue = Color(0xFF7CB9E8)

@Composable
fun ChartScreen(
    vm: TransactionViewModel,
    onBack: () -> Unit, // 雖然參數名叫 onBack，但我們會用來開 Drawer
    onOpenDrawer: () -> Unit = onBack // 為了相容，預設指向 onBack
) {
    // 監聽年份月份與 Tab 變化，重新載入資料
    LaunchedEffect(vm.chartYear, vm.chartMonth, vm.chartTab) {
        vm.loadMonthlyChartData()
    }

    Scaffold(
        containerColor = BeigeBg,
        topBar = {
            Column(Modifier.background(BeigeBg)) {
                // 1. 頂部選單與 Tab 切換
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 8.dp)
                ) {
                    // 左側 Menu Icon
                    IconButton(
                        onClick = onOpenDrawer,
                        modifier = Modifier.align(Alignment.CenterStart).padding(start = 8.dp)
                    ) {
                        Icon(Icons.Default.Menu, null, tint = TextDark)
                    }

                    // 中間 Segmented Toggle (支出/收入/結餘)
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
                                    .background(if (isSelected) Color.White else Color.Transparent)
                                    .padding(horizontal = 24.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text,
                                    color = if (isSelected) TextDark else Color.Gray,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                // 2. 時間篩選器 (月/年/自訂) + 月份切換
                TimeFilterSection(vm)
            }
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 3. 每日趨勢圖卡片
            DailyTrendCard(vm)

            // 4. 交易類型 (甜甜圈圖 + 圖例)
            TypeAnalysisCard(vm)

            // 5. 支出明細列表
            DetailListCard(vm)

            Spacer(Modifier.height(40.dp))
        }
    }
}

// --- 元件: 時間篩選區塊 ---
@Composable
fun TimeFilterSection(vm: TransactionViewModel) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        // 月/年/自訂 膠囊按鈕
        Row(
            Modifier
                .border(1.dp, Color.Gray, RoundedCornerShape(50))
                .clip(RoundedCornerShape(50))
                .background(Color.White)
                .padding(2.dp)
        ) {
            val filters = listOf("月", "年", "自訂")
            var selectedFilter by remember { mutableIntStateOf(0) } // 0=月

            filters.forEachIndexed { index, text ->
                val isSelected = selectedFilter == index
                Box(
                    Modifier
                        .clickable { selectedFilter = index }
                        .width(80.dp) // 固定寬度讓它看起來像圖片
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text, color = if (isSelected) TextDark else Color.LightGray)
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // 月份切換 ( < 2025年 11月 > )
        Row(verticalAlignment = Alignment.CenterVertically) {
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

            Text(
                "${vm.chartYear}年 ${vm.chartMonth}月",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

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

// --- 元件: 每日趨勢圖卡片 ---
@Composable
fun DailyTrendCard(vm: TransactionViewModel) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // 隨便用個 Icon 模擬圖片中的折線圖示，旋轉90度像N
            Spacer(Modifier.width(8.dp))
            Text("每日趨勢", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
        }

        Spacer(Modifier.height(8.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderGray),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(220.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                // 標題與平均值
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(12.dp, 3.dp).background(DashBlue))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "平均每日 : ${if(vm.chartTab==0) "-" else ""}${vm.currency}${vm.averageDaily}",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }

                Spacer(Modifier.height(16.dp))

                // 長條圖 Canvas
                val data = vm.monthlyDailyStats
                val daysInMonth = 31 // 簡單起見固定 31

                Canvas(Modifier.fillMaxSize()) {
                    val barWidth = 8.dp.toPx()
                    val spacing = (size.width - (daysInMonth * barWidth)) / (daysInMonth - 1)
                    val maxVal = (data.values.maxOrNull() ?: 1).coerceAtLeast(vm.averageDaily * 2).toFloat()

                    // 畫平均線 (虛線)
                    val avgY = size.height - (vm.averageDaily / maxVal * size.height)
                    drawLine(
                        color = DashBlue,
                        start = Offset(0f, avgY),
                        end = Offset(size.width, avgY),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f)),
                        strokeWidth = 2f
                    )

                    // 畫長條
                    for (i in 1..daysInMonth) {
                        val amount = data[i] ?: 0
                        if (amount > 0) {
                            val barHeight = (amount / maxVal) * size.height
                            val x = (i - 1) * (barWidth + spacing)
                            val y = size.height - barHeight

                            drawRect(
                                color = ChartYellow,
                                topLeft = Offset(x, y),
                                size = Size(barWidth, barHeight)
                            )
                        }
                    }

                    // 畫 X 軸標籤 (1, 3, 5...)
                    val textPaint = android.graphics.Paint().apply {
                        color = android.graphics.Color.LTGRAY
                        textSize = 24f
                        textAlign = android.graphics.Paint.Align.CENTER
                    }

                    for (i in 1..daysInMonth step 2) {
                        val x = (i - 1) * (barWidth + spacing) + barWidth / 2
                        drawContext.canvas.nativeCanvas.drawText(
                            "$i", x, size.height + 30f, textPaint
                        )
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
            Spacer(Modifier.width(8.dp))
            Text("交易類型", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
        }

        Spacer(Modifier.height(8.dp))

        // 甜甜圈圖區塊
        Box(Modifier.fillMaxWidth().height(260.dp), contentAlignment = Alignment.Center) {
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

            // 中間文字
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(if (vm.chartTab == 1) "總收入" else "總支出", fontSize = 12.sp, color = Color.Gray)
                Text("${vm.currency}$total", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
            }
        }

        // 下方圖例列表 (Card)
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
                                modifier = Modifier.weight(1f).padding(vertical = 8.dp)
                            ) {
                                Box(Modifier.size(12.dp).background(colors[colorIndex]))
                                Spacer(Modifier.width(12.dp))
                                Text(vm.getCategoryName(item.key), fontSize = 14.sp, color = TextDark)
//                                Spacer(Modifier.width(1f))
                                Text(
                                    String.format(Locale.getDefault(), "%.2f%%", item.percentage), // ★ 修正：使用 Locale
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

// --- 元件: 支出明細列表 ---
@Composable
fun DetailListCard(vm: TransactionViewModel) {
    val stats = vm.monthlyCategoryStats
    val colors = listOf(ChartBlue, ChartGreen, ChartPink, ChartYellow, Color(0xFFBA68C8), Color(0xFFFF8A65))
    val isExpense = vm.chartTab == 0

    Column {
        Text(if(isExpense) "支出明細" else "收入明細", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
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
                        // 色塊
                        Box(Modifier.size(16.dp).background(color))
                        Spacer(Modifier.width(16.dp))

                        // 名稱
                        Text(vm.getCategoryName(item.key), fontSize = 16.sp, color = TextDark)
                        Spacer(Modifier.width(24.dp))

                        // 筆數 (灰色小字)
                        Text("(${item.count}筆)", fontSize = 12.sp, color = Color.Gray)

                        Spacer(Modifier.weight(1f))

                        // 金額
                        val sign = if (isExpense) "-" else ""
                        Text(
                            "$sign${vm.currency}${item.totalAmount}",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }

                    if (index < stats.lastIndex) {
                        HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                    }
                }
            }
        }
    }
}

// 擴充函式：旋轉
fun Modifier.rotate(degrees: Float) = this.then(
    Modifier.graphicsLayer(rotationZ = degrees)
)