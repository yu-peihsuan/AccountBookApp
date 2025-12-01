package com.example.accountbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.accountbook.screens.*
import com.example.accountbook.viewmodel.TransactionViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AccountBookApp()
        }
    }
}

@Composable
fun AccountBookApp() {
    val vm: TransactionViewModel = viewModel()
    val navController = rememberNavController()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // 判斷起始頁面：若已登入則到 Home，否則到 Login
    val startDest = if (vm.isLoggedIn) "home" else "login"

    ModalNavigationDrawer(
        drawerState = drawerState,
        // Drawer 也要多語系，所以傳入 vm
        drawerContent = {
            if (vm.isLoggedIn) { // 只有登入後才顯示 Drawer 內容
                ModalDrawerSheet {
                    DrawerMenu(
                        vm = vm,
                        onSelect = { route ->
                            // 1. 先關閉側邊欄
                            scope.launch { drawerState.close() }

                            // ★ 修改：攔截 "logout" 指令
                            if (route == "logout") {
                                vm.logout() // 執行 ViewModel 登出
                                navController.navigate("login") {
                                    // 清除 Back Stack，防止按返回鍵回到 App 內部
                                    popUpTo(0) { inclusive = true }
                                }
                            } else {
                                // 2. 其他正常頁面跳轉
                                navController.navigate(route) {
                                    popUpTo("home") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) {
        NavHost(navController = navController, startDestination = startDest) {

            // ★ 1. 登入頁
            composable("login") {
                LoginScreen(
                    vm = vm,
                    onLoginSuccess = {
                        // 登入成功，跳轉首頁，並清空 back stack 防止按返回鍵回到登入頁
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }

            // ★ 2. 首頁
            composable("home") {
                HomeScreen(
                    vm = vm,
                    nav = navController,
                    onOpenDrawer = { scope.launch { drawerState.open() } }
                )
            }

            // ★ 3. 記帳頁
            composable("add") {
                AddTransactionScreen(
                    vm = vm,
                    onBack = { navController.popBackStack() },
                    onAdd = { title, amount, type, date, categoryKey ->
                        vm.addTransaction(title, amount, type, date, categoryKey)
                    }
                )
            }

            // ★ 4. 圖表頁
            composable("chart") {
                ChartScreen(vm = vm, onBack = { navController.popBackStack() })
            }

            // ★ 5. 設定頁
            composable("setting") {
                SettingScreen(
                    vm = vm,
                    onBack = { navController.popBackStack() },
                    onLogout = {
                        vm.logout()
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    onOpenDrawer = { scope.launch { drawerState.open() } }
                )
            }
        }
    }
}