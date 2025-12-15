package com.example.accountbook.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.accountbook.viewmodel.TransactionViewModel
import java.io.File

@Composable
fun DrawerMenu(vm: TransactionViewModel, onSelect:(String)->Unit) {

    val items = listOf(
        Triple("交易紀錄", "home", Color(0xFFDCD5C7)),
        Triple("記帳本", "add", Color(0xFFDCD5C7)),
        Triple("圖表分析", "chart", Color(0xFFDCD5C7)),
        Triple("功能設定", "setting", Color(0xFFDCD5C7))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F3E9))
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween // 讓登出固定在底部
    ) {

        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {

            //使用者頭像、名稱橫向顯示
            Row(verticalAlignment = Alignment.CenterVertically) {
                // 顯示大頭照
                if (vm.userAvatar.isNotEmpty()) {
                    AsyncImage(
                        model = File(vm.userAvatar),
                        contentDescription = "User Avatar",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "User Avatar",
                        modifier = Modifier.size(48.dp),
                        tint = Color(0xFF4A463F)
                    )
                }

                Spacer(Modifier.width(14.dp))

                Column {
                    Text(vm.userName.ifEmpty { "U11216004" },
                        fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF4A463F))
                    Text(vm.userEmail.ifEmpty { "帳號資訊" },
                        fontSize = 20.sp, color = Color.Gray)
                }
            }

            Spacer(Modifier.height(14.dp))

            //功能列表
            items.forEach { (text, route, bgColor) ->
                DrawerItem(text, route, onSelect, bgColor)
            }
        }

        //登出按鈕置底
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE57373)) // 紅色區隔
                .clickable { onSelect("logout") }
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("登出", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun DrawerItem(text:String, route:String, onSelect:(String)->Unit, bgColor: Color) {
    // 圓角按鈕
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor) // 使用指定的背景色
            .clickable { onSelect(route) }
            .padding(horizontal = 24.dp, vertical = 16.dp), // 調整內邊距
        contentAlignment = Alignment.CenterStart
    ){
        Text(text, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color(0xFF4A463F))
    }
}