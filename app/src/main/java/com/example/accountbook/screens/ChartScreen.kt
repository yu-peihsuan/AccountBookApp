@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.accountbook.screens

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import com.example.accountbook.viewmodel.TransactionViewModel

@Composable
fun ChartScreen(vm: TransactionViewModel, onBack: () -> Unit) {

    val strings = vm.currentStrings

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(strings.chartTitle) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {

            Text(strings.chartPie)
            Spacer(Modifier.height(12.dp))

            Box(
                Modifier.fillMaxWidth().height(200.dp),
                // 之後會放PieChart
            )

            Spacer(Modifier.height(24.dp))

            Text(strings.chartBar)
            Spacer(Modifier.height(12.dp))

            Box(
                Modifier.fillMaxWidth().height(200.dp),
                // 這邊之後加BarChart
            )
        }
    }
}