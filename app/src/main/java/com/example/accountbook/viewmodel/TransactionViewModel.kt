package com.example.accountbook.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.accountbook.data.DBHandler
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// ---------------- 多語系資源定義 ----------------
interface StringResources {
    val loginTitle: String
    val registerTitle: String
    val fieldName: String
    val fieldEmail: String
    val fieldPassword: String
    val btnLogin: String
    val btnRegister: String
    val switchRegister: String
    val switchLogin: String
    val errorEmptyField: String
    val errorLoginFailed: String
    val tabExpense: String
    val tabIncome: String
    val balance: String
    val budget: String
    val remain: String
    val historyTitle: String
    val menuHome: String
    val menuAdd: String
    val menuChart: String
    val menuSetting: String
    val menuLogout: String
    val inputNote: String
    val btnDone: String
    val btnConfirm: String
    val btnCancel: String
    val categoryBreakfast: String
    val categoryLunch: String
    val categoryDinner: String
    val categoryDrink: String
    val categoryTraffic: String
    val categoryShopping: String
    val categoryDaily: String
    val categoryEntertainment: String
    val categoryRent: String
    val categoryBills: String
    val categoryOther: String
    val categoryAdd: String
    val settingTitle: String
    val sectionAccount: String
    val labelAccount: String
    val labelName: String
    val sectionFunction: String
    val labelCurrency: String
    val labelBudget: String
    val labelExport: String
    val labelLanguage: String
    val labelHelp: String
    val optionTraditionalChinese: String
    val optionEnglish: String
    val chartTitle: String
    val chartPie: String
    val chartBar: String
    val dateFormat: String
    val dayFormat: String
}

object StringsZH : StringResources {
    override val loginTitle = "登入"
    override val registerTitle = "註冊帳號"
    override val fieldName = "姓名"
    override val fieldEmail = "電子郵件"
    override val fieldPassword = "密碼"
    override val btnLogin = "登入"
    override val btnRegister = "註冊"
    override val switchRegister = "沒有帳號？點此註冊"
    override val switchLogin = "已有帳號？點此登入"
    override val errorEmptyField = "欄位不能為空"
    override val errorLoginFailed = "登入失敗，請檢查帳號密碼"
    override val tabExpense = "支出"
    override val tabIncome = "收入"
    override val balance = "結餘"
    override val budget = "預算"
    override val remain = "剩餘"
    override val historyTitle = "交易紀錄"
    override val menuHome = "交易紀錄"
    override val menuAdd = "記帳本"
    override val menuChart = "圖表分析"
    override val menuSetting = "功能設定"
    override val menuLogout = "登出"
    override val inputNote = "輸入備註"
    override val btnDone = "完成"
    override val btnConfirm = "確定"
    override val btnCancel = "取消"
    override val categoryBreakfast = "早餐"
    override val categoryLunch = "午餐"
    override val categoryDinner = "晚餐"
    override val categoryDrink = "飲料"
    override val categoryTraffic = "交通"
    override val categoryShopping = "購物"
    override val categoryDaily = "日用品"
    override val categoryEntertainment = "娛樂"
    override val categoryRent = "房租"
    override val categoryBills = "生活繳費"
    override val categoryOther = "其他"
    override val categoryAdd = "新增分類"
    override val settingTitle = "設定"
    override val sectionAccount = "帳號管理"
    override val labelAccount = "登入帳號"
    override val labelName = "使用者名稱"
    override val sectionFunction = "功能"
    override val labelCurrency = "貨幣符號"
    override val labelBudget = "每月預算設定"
    override val labelExport = "資料匯出"
    override val labelLanguage = "語言"
    override val labelHelp = "使用說明"
    override val optionTraditionalChinese = "中文(繁體)"
    override val optionEnglish = "English"
    override val chartTitle = "圖表分析"
    override val chartPie = "支出分類 (圓餅圖)"
    override val chartBar = "近期每日支出 (長條圖)"
    override val dateFormat = "yyyy/MM/dd"
    override val dayFormat = "EEEE"
}

object StringsEN : StringResources {
    override val loginTitle = "Login"
    override val registerTitle = "Register"
    override val fieldName = "Name"
    override val fieldEmail = "Email"
    override val fieldPassword = "Password"
    override val btnLogin = "Login"
    override val btnRegister = "Sign Up"
    override val switchRegister = "No account? Sign up here"
    override val switchLogin = "Have an account? Login here"
    override val errorEmptyField = "Fields cannot be empty"
    override val errorLoginFailed = "Login failed, check credentials"
    override val tabExpense = "Expense"
    override val tabIncome = "Income"
    override val balance = "Balance"
    override val budget = "Budget"
    override val remain = "Remain"
    override val historyTitle = "History"
    override val menuHome = "History"
    override val menuAdd = "Add Record"
    override val menuChart = "Analysis"
    override val menuSetting = "Settings"
    override val menuLogout = "Logout"
    override val inputNote = "Add a note"
    override val btnDone = "Done"
    override val btnConfirm = "OK"
    override val btnCancel = "Cancel"
    override val categoryBreakfast = "Breakfast"
    override val categoryLunch = "Lunch"
    override val categoryDinner = "Dinner"
    override val categoryDrink = "Drink"
    override val categoryTraffic = "Traffic"
    override val categoryShopping = "Shopping"
    override val categoryDaily = "Daily"
    override val categoryEntertainment = "Fun"
    override val categoryRent = "Rent"
    override val categoryBills = "Bills"
    override val categoryOther = "Other"
    override val categoryAdd = "Add"
    override val settingTitle = "Settings"
    override val sectionAccount = "Account"
    override val labelAccount = "Email"
    override val labelName = "Username"
    override val sectionFunction = "Functions"
    override val labelCurrency = "Currency"
    override val labelBudget = "Monthly Budget"
    override val labelExport = "Export Data"
    override val labelLanguage = "Language"
    override val labelHelp = "Help"
    override val optionTraditionalChinese = "Traditional Chinese"
    override val optionEnglish = "English"
    override val chartTitle = "Analysis"
    override val chartPie = "Expense by Category (Pie Chart)"
    override val chartBar = "Daily Expenses (Bar Chart)"
    override val dateFormat = "MM/dd/yyyy"
    override val dayFormat = "EEEE"
}

// ---------------- ViewModel ----------------

data class Transaction(
    val id: Long = 0,
    val date: String,
    val day: String,
    val title: String,
    val amount: Int,
    val type: String,
    val categoryKey: String = ""
) : Serializable

data class CategoryStat(
    val key: String,
    val totalAmount: Int,
    val count: Int,
    val percentage: Float
)

class TransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val dbHandler = DBHandler(context)
    private val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    var budget by mutableIntStateOf(8000)
        private set
    var currency by mutableStateOf("NT$")
        private set
    var language by mutableStateOf("中文(繁體)")
        private set

    var userName by mutableStateOf("")
        private set
    var userEmail by mutableStateOf("")
        private set
    var isLoggedIn by mutableStateOf(false)
        private set

    var currentUserId by mutableIntStateOf(-1)
        private set

    val currentStrings: StringResources
        get() = if (language == "English") StringsEN else StringsZH

    private val _transactions = mutableStateListOf<Transaction>()
    val transactions: List<Transaction> get() = _transactions

    // --- Chart Screen States ---
    var chartYear by mutableIntStateOf(Calendar.getInstance().get(Calendar.YEAR))
    var chartMonth by mutableIntStateOf(Calendar.getInstance().get(Calendar.MONTH) + 1)

    // Tab: 0=支出, 1=收入, 2=結餘
    var chartTab by mutableIntStateOf(0)

    // 時間模式 0=月, 1=年, 2=自訂
    var chartTimeMode by mutableIntStateOf(0)

    // 自訂模式的起訖日期 (Milliseconds)
    var customStartDateMillis by mutableLongStateOf(System.currentTimeMillis())
    var customEndDateMillis by mutableLongStateOf(System.currentTimeMillis())

    // 通用的圖表資料點
    var chartDataPoints by mutableStateOf<List<Pair<String, Int>>>(emptyList())
        private set

    // 平均
    var averageDaily by mutableIntStateOf(0)
        private set

    // 分類統計列表
    var monthlyCategoryStats by mutableStateOf<List<CategoryStat>>(emptyList())
        private set

    // 總金額 (或總結餘)
    var monthlyTotal by mutableIntStateOf(0)
        private set

    // 舊的 (給首頁用)
    var categoryTotals by mutableStateOf<Map<String, Int>>(emptyMap())
        private set
    var dailyTotals by mutableStateOf<List<Pair<String, Int>>>(emptyList())
        private set

    init {
        loadSettings()
        checkLoginStatus()
        loadData()

        // 初始化自訂日期為本月第一天到今天
        val c = Calendar.getInstance()
        c.set(Calendar.DAY_OF_MONTH, 1)
        customStartDateMillis = c.timeInMillis
    }

    // ★ 快速設定區間
    fun applyQuickRange(rangeType: String) {
        val calendar = Calendar.getInstance()
        val today = System.currentTimeMillis()
        calendar.timeInMillis = today

        when (rangeType) {
            "7days" -> {
                customEndDateMillis = today
                calendar.add(Calendar.DAY_OF_YEAR, -6)
                customStartDateMillis = calendar.timeInMillis
            }
            "30days" -> {
                customEndDateMillis = today
                calendar.add(Calendar.DAY_OF_YEAR, -29)
                customStartDateMillis = calendar.timeInMillis
            }
            "thisMonth" -> {
                customEndDateMillis = today
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                customStartDateMillis = calendar.timeInMillis
            }
        }
    }

    fun getCategoryName(key: String): String {
        val s = currentStrings
        return when (key) {
            "breakfast" -> s.categoryBreakfast
            "lunch" -> s.categoryLunch
            "dinner" -> s.categoryDinner
            "drink" -> s.categoryDrink
            "traffic" -> s.categoryTraffic
            "shopping" -> s.categoryShopping
            "daily" -> s.categoryDaily
            "entertainment" -> s.categoryEntertainment
            "rent" -> s.categoryRent
            "bills" -> s.categoryBills
            "other" -> s.categoryOther
            "add" -> s.categoryAdd
            else -> ""
        }
    }

    fun register(name: String, email: String, pass: String): String {
        if (dbHandler.checkUserExists(name)) return "使用者名稱已存在"
        if (dbHandler.checkEmailExists(email)) return "此 Email 已被註冊"

        val newUserId = dbHandler.addUser(name, email, pass)

        if (newUserId != -1L) {
            prefs.edit()
                .putInt("user_id", newUserId.toInt())
                .putString("user_name", name)
                .putString("user_email", email)
                .putBoolean("is_logged_in", true)
                .apply()

            currentUserId = newUserId.toInt()
            userName = name
            userEmail = email
            isLoggedIn = true
            _transactions.clear()
            return ""
        } else {
            return "註冊失敗，資料庫錯誤"
        }
    }

    fun login(email: String, pass: String): Boolean {
        val userInfo = dbHandler.validateUser(email, pass)

        if (userInfo != null) {
            val (id, name) = userInfo
            prefs.edit()
                .putInt("user_id", id)
                .putString("user_name", name)
                .putString("user_email", email)
                .putBoolean("is_logged_in", true)
                .apply()

            currentUserId = id
            userName = name
            userEmail = email
            isLoggedIn = true
            loadData()
            return true
        }
        return false
    }

    private fun checkLoginStatus() {
        if (!prefs.contains("user_email")) {
            logout()
        }

        isLoggedIn = prefs.getBoolean("is_logged_in", false)
        if (isLoggedIn) {
            currentUserId = prefs.getInt("user_id", -1)
            userName = prefs.getString("user_name", "") ?: ""
            userEmail = prefs.getString("user_email", "") ?: ""
        }
    }

    fun logout() {
        prefs.edit()
            .putBoolean("is_logged_in", false)
            .remove("user_id")
            .apply()

        isLoggedIn = false
        userName = ""
        userEmail = ""
        currentUserId = -1
        _transactions.clear()
    }

    fun updateBudget(newBudget: Int) {
        budget = newBudget
        prefs.edit().putInt("monthly_budget", newBudget).apply()
    }

    fun updateCurrency(newCurrency: String) {
        currency = newCurrency
        prefs.edit().putString("currency_symbol", newCurrency).apply()
    }

    fun updateLanguage(newLang: String) {
        language = newLang
        prefs.edit().putString("app_language", newLang).apply()
    }

    fun addTransaction(title: String, amount: Int, type: String, dateMillis: Long, categoryKey: String) {
        if (currentUserId == -1) return

        val formatD = SimpleDateFormat(currentStrings.dateFormat, if(language=="English") Locale.US else Locale.TAIWAN)
        val formatW = SimpleDateFormat(currentStrings.dayFormat, if(language=="English") Locale.US else Locale.TAIWAN)

        val dateStr = formatD.format(dateMillis)
        val dayStr = formatW.format(dateMillis)

        dbHandler.addTransaction(currentUserId, dateStr, dayStr, title, amount, type, categoryKey)
        loadData()
    }

    fun updateTransaction(id: Long, title: String, amount: Int, type: String, dateMillis: Long, categoryKey: String) {
        val formatD = SimpleDateFormat(currentStrings.dateFormat, if(language=="English") Locale.US else Locale.TAIWAN)
        val formatW = SimpleDateFormat(currentStrings.dayFormat, if(language=="English") Locale.US else Locale.TAIWAN)

        val dateStr = formatD.format(dateMillis)
        val dayStr = formatW.format(dateMillis)

        dbHandler.updateTransaction(id, dateStr, dayStr, title, amount, type, categoryKey)
        loadData()
    }

    fun deleteTransaction(id: Long) {
        dbHandler.deleteTransaction(id)
        loadData()
    }

    fun getTransactionById(id: Long): Transaction? {
        return dbHandler.getTransactionById(id)
    }

    private fun isExpense(type: String): Boolean = type == "支出" || type == "Expense"
    private fun isIncome(type: String): Boolean = type == "收入" || type == "Income"

    fun totalExpense(): Int = _transactions.filter { isExpense(it.type) }.sumOf { it.amount }
    fun totalIncome(): Int = _transactions.filter { isIncome(it.type) }.sumOf { it.amount }
    fun monthBalance(): Int = totalIncome() - totalExpense()

    fun remain(): Int = budget - totalExpense()

    fun percentRemain(): Int {
        val r = remain()
        return if (budget > 0) ((r.toFloat() / budget) * 100).toInt() else 0
    }

    private fun loadData() {
        if (currentUserId != -1) {
            val list = dbHandler.getAllTransactions(currentUserId)
            _transactions.clear()
            _transactions.addAll(list)
            categoryTotals = dbHandler.getCategoryTotals(currentUserId)
            dailyTotals = dbHandler.getRecentDailyTotals(currentUserId)
            loadMonthlyChartData()
        } else {
            _transactions.clear()
        }
    }

    // ★ 載入圖表頁面的詳細數據 (根據 chartTimeMode 切換月/年/自訂)
    fun loadMonthlyChartData() {
        if (currentUserId == -1) return

        var newChartData = mutableListOf<Pair<String, Int>>()
        var totalSum = 0
        var rawCategoryStats: List<Triple<String, Int, Int>> = emptyList()

        if (chartTab == 2) {
            // ================== 結餘模式 (Balance) ==================
            // 計算：收入 - 支出
            if (chartTimeMode == 1) {
                // --- 年模式 ---
                val inc = dbHandler.getYearlyMonthlyStats(currentUserId, chartYear, "收入")
                val exp = dbHandler.getYearlyMonthlyStats(currentUserId, chartYear, "支出")

                for (m in 1..12) {
                    val iVal = inc[m] ?: 0
                    val eVal = exp[m] ?: 0
                    val balance = iVal - eVal
                    newChartData.add(Pair("$m", balance))
                    totalSum += balance
                }
                rawCategoryStats = dbHandler.getYearlyCategoryStats(currentUserId, chartYear, "支出")

            } else if (chartTimeMode == 2) {
                // --- 自訂區間 ---
                val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                val startStr = sdf.format(Date(customStartDateMillis))
                val endStr = sdf.format(Date(customEndDateMillis))

                val inc = dbHandler.getRangeDailyStats(currentUserId, startStr, endStr, "收入")
                val exp = dbHandler.getRangeDailyStats(currentUserId, startStr, endStr, "支出")

                val diff = customEndDateMillis - customStartDateMillis
                val daysCount = (diff / (1000 * 60 * 60 * 24)).toInt() + 1
                val cal = Calendar.getInstance()
                cal.timeInMillis = customStartDateMillis

                if (daysCount > 60) {
                    val sortedKeys = (inc.keys + exp.keys).sorted()
                    for (dateKey in sortedKeys) {
                        val label = dateKey.substring(5)
                        val iVal = inc[dateKey] ?: 0
                        val eVal = exp[dateKey] ?: 0
                        newChartData.add(Pair(label, iVal - eVal))
                        totalSum += (iVal - eVal)
                    }
                } else {
                    for (i in 0 until daysCount) {
                        val dateKey = sdf.format(cal.time)
                        val label = dateKey.substring(5)
                        val iVal = inc[dateKey] ?: 0
                        val eVal = exp[dateKey] ?: 0
                        newChartData.add(Pair(label, iVal - eVal))
                        totalSum += (iVal - eVal)
                        cal.add(Calendar.DAY_OF_YEAR, 1)
                    }
                }
                rawCategoryStats = dbHandler.getRangeCategoryStats(currentUserId, startStr, endStr, "支出")

            } else {
                // --- 月模式 ---
                val inc = dbHandler.getMonthlyDailyStats(currentUserId, chartYear, chartMonth, "收入")
                val exp = dbHandler.getMonthlyDailyStats(currentUserId, chartYear, chartMonth, "支出")
                val daysInMonth = getDaysInMonth(chartYear, chartMonth)

                for (d in 1..daysInMonth) {
                    val iVal = inc[d] ?: 0
                    val eVal = exp[d] ?: 0
                    val balance = iVal - eVal
                    newChartData.add(Pair("$d", balance))
                    totalSum += balance
                }
                rawCategoryStats = dbHandler.getMonthlyCategoryStats(currentUserId, chartYear, chartMonth, "支出")
            }

            // 平均值 (結餘的平均)
            averageDaily = if (newChartData.isNotEmpty()) totalSum / newChartData.size else 0
            monthlyTotal = totalSum // 這裡代表總結餘

        } else {
            // ================== 支出 / 收入模式 ==================
            val typeFilter = if (chartTab == 1) "收入" else "支出"

            if (chartTimeMode == 1) {
                // === 年檢視模式 ===
                val monthlyStats = dbHandler.getYearlyMonthlyStats(currentUserId, chartYear, typeFilter)
                for (m in 1..12) {
                    val amount = monthlyStats[m] ?: 0
                    newChartData.add(Pair("$m", amount))
                }
                totalSum = monthlyStats.values.sum()
                rawCategoryStats = dbHandler.getYearlyCategoryStats(currentUserId, chartYear, typeFilter)

            } else if (chartTimeMode == 2) {
                // === 自訂區間模式 ===
                val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                val startStr = sdf.format(Date(customStartDateMillis))
                val endStr = sdf.format(Date(customEndDateMillis))
                val dailyStats = dbHandler.getRangeDailyStats(currentUserId, startStr, endStr, typeFilter)

                val diff = customEndDateMillis - customStartDateMillis
                val daysCount = (diff / (1000 * 60 * 60 * 24)).toInt() + 1
                val cal = Calendar.getInstance()
                cal.timeInMillis = customStartDateMillis

                if (daysCount > 60) {
                    val sortedKeys = dailyStats.keys.sorted()
                    for (dateKey in sortedKeys) {
                        val label = dateKey.substring(5)
                        newChartData.add(Pair(label, dailyStats[dateKey] ?: 0))
                    }
                } else {
                    for (i in 0 until daysCount) {
                        val dateKey = sdf.format(cal.time)
                        val label = dateKey.substring(5)
                        val amount = dailyStats[dateKey] ?: 0
                        newChartData.add(Pair(label, amount))
                        cal.add(Calendar.DAY_OF_YEAR, 1)
                    }
                }
                totalSum = dailyStats.values.sum()
                rawCategoryStats = dbHandler.getRangeCategoryStats(currentUserId, startStr, endStr, typeFilter)

            } else {
                // === 月檢視模式 ===
                val monthlyStats = dbHandler.getMonthlyDailyStats(currentUserId, chartYear, chartMonth, typeFilter)
                val daysInMonth = getDaysInMonth(chartYear, chartMonth)
                for (d in 1..daysInMonth) {
                    val amount = monthlyStats[d] ?: 0
                    newChartData.add(Pair("$d", amount))
                }
                totalSum = monthlyStats.values.sum()
                rawCategoryStats = dbHandler.getMonthlyCategoryStats(currentUserId, chartYear, chartMonth, typeFilter)
            }

            monthlyTotal = totalSum
            averageDaily = if (newChartData.isNotEmpty()) totalSum / newChartData.size else 0
        }

        chartDataPoints = newChartData

        // 更新分類佔比
        val statsTotal = rawCategoryStats.sumOf { it.second }
        monthlyCategoryStats = rawCategoryStats.map { (key, sum, count) ->
            val pct = if (statsTotal > 0) (sum.toFloat() / statsTotal) * 100 else 0f
            CategoryStat(key, sum, count, pct)
        }
    }

    // ★★★ 取得特定分類在指定範圍內的詳細交易列表 (用於點擊跳轉) ★★★
    fun getTransactionsForCategoryDetail(categoryKey: String): List<Transaction> {
        if (currentUserId == -1) return emptyList()

        // 注意：如果是結餘模式，下方的列表顯示的是「支出」分類，所以這裡 filter 要設為支出
        val typeFilter = if (chartTab == 1) "收入" else "支出"

        var startDate = ""
        var endDate = ""
        var isLikeQuery = false

        if (chartTimeMode == 1) {
            startDate = "$chartYear/%"
            isLikeQuery = true
        } else if (chartTimeMode == 2) {
            val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            startDate = sdf.format(Date(customStartDateMillis))
            endDate = sdf.format(Date(customEndDateMillis))
            isLikeQuery = false
        } else {
            startDate = String.format("%04d/%02d", chartYear, chartMonth) + "%"
            isLikeQuery = true
        }

        return dbHandler.getCategoryTransactions(currentUserId, categoryKey, startDate, endDate, typeFilter, isLikeQuery)
    }

    private fun getDaysInMonth(year: Int, month: Int): Int {
        val cal = Calendar.getInstance()
        cal.set(year, month - 1, 1)
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    private fun loadSettings() {
        budget = prefs.getInt("monthly_budget", 8000)
        currency = prefs.getString("currency_symbol", "NT$") ?: "NT$"
        language = prefs.getString("app_language", "中文(繁體)") ?: "中文(繁體)"
    }
}