@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.accountbook.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import com.example.accountbook.viewmodel.TransactionViewModel

@Composable
fun ChartScreen(vm: TransactionViewModel, onBack: () -> Unit) {

    val strings = vm.currentStrings

    // 進入頁面時重新載入圖表資料
    LaunchedEffect(Unit) {
        vm.loadChartData()
    }

    val categoryData = vm.categoryTotals
    val dailyData = vm.dailyTotals

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(strings.chartTitle, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFFFCF5E8))
            )
        },
        containerColor = Color(0xFFFDFBF7)
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // --- 圓餅圖區塊 ---
            Text(strings.chartPie, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4A463F))

            Box(
                Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                if (categoryData.isEmpty()) {
                    Text("尚無支出資料", color = Color.Gray, modifier = Modifier.align(Alignment.Center))
                } else {
                    PieChartWithLegend(categoryData, vm)
                }
            }

            // --- 長條圖區塊 ---
            Text(strings.chartBar, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4A463F))

            Box(
                Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                if (dailyData.isEmpty()) {
                    Text("尚無支出資料", color = Color.Gray, modifier = Modifier.align(Alignment.Center))
                } else {
                    BarChart(dailyData)
                }
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}

// ---------------- 圓餅圖元件 ----------------

@Composable
fun PieChartWithLegend(data: Map<String, Int>, vm: TransactionViewModel) {
    val total = data.values.sum()
    val colors = listOf(
        Color(0xFFE57373), Color(0xFF81C784), Color(0xFF64B5F6), Color(0xFFFFD54F),
        Color(0xFFBA68C8), Color(0xFF4DB6AC), Color(0xFFFF8A65), Color(0xFFA1887F)
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(200.dp)) {
                var startAngle = -90f
                data.keys.forEachIndexed { index, key ->
                    val value = data[key] ?: 0
                    val sweepAngle = (value.toFloat() / total) * 360f
                    val color = colors[index % colors.size]

                    drawArc(
                        color = color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = true,
                        style = Fill
                    )
                    startAngle += sweepAngle
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // 圖例
        Column(Modifier.fillMaxWidth()) {
            data.keys.forEachIndexed { index, key ->
                val value = data[key] ?: 0
                val color = colors[index % colors.size]
                val percent = (value.toFloat() / total * 100).toInt()
                val name = vm.getCategoryName(key)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Box(Modifier.size(12.dp).background(color, RoundedCornerShape(2.dp)))
                    Spacer(Modifier.width(8.dp))
                    Text(name, modifier = Modifier.weight(1f), fontSize = 14.sp)
                    Text("${vm.currency}$value ($percent%)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

// ---------------- 長條圖元件 ----------------

@Composable
fun BarChart(data: List<Pair<String, Int>>) {
    val maxVal = data.maxOfOrNull { it.second } ?: 1

    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        data.forEach { (date, value) ->
            // 只取日期的 "月/日" 部分 (假設格式 yyyy/MM/dd)
            val shortDate = if (date.length >= 10) date.substring(5) else date

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.weight(1f)
            ) {
                // 金額標籤
                Text(value.toString(), fontSize = 10.sp, color = Color.Gray)

                Spacer(Modifier.height(4.dp))

                // 長條
                val barHeightFraction = (value.toFloat() / maxVal).coerceIn(0.1f, 1f) // 最小高度 10%
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .fillMaxHeight(barHeightFraction)
                        .background(Color(0xFF7CB9E8), RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                )

                Spacer(Modifier.height(4.dp))

                // 日期標籤
                Text(shortDate, fontSize = 10.sp, color = Color.DarkGray)
            }
        }
    }
}