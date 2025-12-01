@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.accountbook.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MonthPickerDialog(
    year: Int,
    month: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {

    var tempYear by remember { mutableIntStateOf(year) }

    AlertDialog(onDismissRequest = onDismiss) {

        Column(
            Modifier.background(Color.White, RoundedCornerShape(16.dp))
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = { tempYear-- }) { Text("←") }
                Text("$tempYear 年", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                TextButton(onClick = { tempYear++ }) { Text("→") }
            }

            (1..12).chunked(4).forEach { row ->
                Row {
                    row.forEach { m ->
                        Box(
                            Modifier
                                .padding(6.dp)
                                .size(55.dp)
                                .background(
                                    if (tempYear == year && m == month) Color(0xFFEAC45D)
                                    else Color(0xFFEFEFEF),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    onConfirm(tempYear, m)
                                    onDismiss()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("${m}月", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(Modifier.height(4.dp))
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    }
}