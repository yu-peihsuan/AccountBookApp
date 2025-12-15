@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.accountbook.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.accountbook.viewmodel.TransactionViewModel

@Composable
fun CategoryDetailScreen(
    vm: TransactionViewModel,
    categoryKey: String,
    onBack: () -> Unit,
    onEditTransaction: (Long) -> Unit
) {
    // 取得資料
    val transactions = remember(categoryKey, vm.chartYear, vm.chartMonth, vm.chartTimeMode, vm.customStartDateMillis, vm.customEndDateMillis) {
        vm.getTransactionsForCategoryDetail(categoryKey)
    }

    val currency = vm.currency
    val categoryName = vm.getCategoryName(categoryKey)
    val totalAmount = transactions.sumOf { it.amount }

    // 依照日期分組
    val list = remember(transactions) {
        transactions.groupBy { "${it.date} ${it.day}" }
            .map { (dateKey, txList) ->
                val dayTotal = txList.sumOf { it.amount }
                // 因為是分類明細，通常都是同類型(都是支出或都是收入)，所以不用計算 net balance
                Triple(dateKey, txList, dayTotal)
            }
            .sortedByDescending { it.first }
    }

    Scaffold(
        containerColor = AppBgColor, // 使用 HomeScreen 定義的背景色
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(categoryName, fontWeight = FontWeight.Bold, color = TextGray)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = TextGray)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = TopBarColor
                )
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding)) {
            // 總額顯示區塊
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("總計", fontSize = 14.sp, color = Color.Gray)
                    Text("$currency$totalAmount", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextGray)
                }
            }

            // 列表
            LazyColumn(
                Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (list.isEmpty()) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("無交易紀錄", color = Color.Gray)
                        }
                    }
                }

                items(list) { (date, txList, _) ->
                    DetailDayGroup(date, txList, currency, vm, onEditTransaction)
                }

                item { Spacer(Modifier.height(30.dp)) }
            }
        }
    }
}

@Composable
fun DetailDayGroup(
    date: String,
    transactions: List<com.example.accountbook.viewmodel.Transaction>,
    currency: String,
    vm: TransactionViewModel,
    onItemClick: (Long) -> Unit
) {
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
    ) {
        Column {
            // 日期標題
            Text(
                text = date,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                modifier = Modifier.padding(top = 12.dp, start = 16.dp, bottom = 4.dp)
            )

            HorizontalDivider(color = Color(0xFFF5F5F5), thickness = 1.dp)

            transactions.forEachIndexed { index, tx ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable { onItemClick(tx.id) }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 圖示
                    Icon(getIconByKey(tx.categoryKey), null, tint = TextGray, modifier = Modifier.size(24.dp))

                    Spacer(Modifier.width(16.dp))

                    // 標題與備註
                    Text(
                        if (tx.title.isNotEmpty()) tx.title else vm.getCategoryName(tx.categoryKey),
                        fontSize = 18.sp,
                        color = TextGray,
                        modifier = Modifier.weight(1f)
                    )

                    // 金額
                    Text(
                        "$currency${tx.amount}",
                        fontSize = 18.sp,
                        color = TextGray,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (index != transactions.lastIndex)
                    HorizontalDivider(color = Color(0xFFFAFAFA), thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}