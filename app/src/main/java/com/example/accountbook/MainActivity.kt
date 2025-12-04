package com.example.accountbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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

    val startDest = if (vm.isLoggedIn) "home" else "login"

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            if (vm.isLoggedIn) {
                ModalDrawerSheet {
                    DrawerMenu(
                        vm = vm,
                        onSelect = { route ->
                            scope.launch { drawerState.close() }
                            if (route == "logout") {
                                vm.logout()
                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            } else {
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

            composable("login") {
                LoginScreen(
                    vm = vm,
                    onLoginSuccess = {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }

            composable("home") {
                HomeScreen(
                    vm = vm,
                    nav = navController,
                    onOpenDrawer = { scope.launch { drawerState.open() } }
                )
            }

            composable(
                route = "add?id={id}",
                arguments = listOf(navArgument("id") {
                    type = NavType.LongType
                    defaultValue = -1L
                })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong("id") ?: -1L
                AddTransactionScreen(
                    vm = vm,
                    transactionId = id,
                    onBack = { navController.popBackStack() }
                )
            }

            // ★ 修正：ChartScreen 的參數傳遞
            composable("chart") {
                ChartScreen(
                    vm = vm,
                    onBack = { navController.popBackStack() }, // 這個給 onBack
                    onOpenDrawer = { scope.launch { drawerState.open() } } // 這個給 Drawer
                )
            }

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