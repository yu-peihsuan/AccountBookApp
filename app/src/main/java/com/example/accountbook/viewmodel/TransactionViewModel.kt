package com.example.accountbook.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.accountbook.data.DBHandler
import java.io.File
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
    // Expense Categories
    val categoryBreakfast: String
    val categoryLunch: String
    val categoryDinner: String
    val categoryDrink: String
    val categorySnack: String
    val categoryTraffic: String
    val categoryShopping: String
    val categoryDaily: String
    val categoryEntertainment: String
    val categoryRent: String
    val categoryBills: String
    val categoryOther: String
    // Income Categories
    val categorySalary: String
    val categoryBonus: String
    val categoryRewards: String

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
    // Expense
    override val categoryBreakfast = "早餐"
    override val categoryLunch = "午餐"
    override val categoryDinner = "晚餐"
    override val categoryDrink = "飲料"
    override val categorySnack = "點心"
    override val categoryTraffic = "交通"
    override val categoryShopping = "購物"
    override val categoryDaily = "日用品"
    override val categoryEntertainment = "娛樂"
    override val categoryRent = "房租"
    override val categoryBills = "生活繳費"
    override val categoryOther = "其他"
    // Income
    override val categorySalary = "薪水"
    override val categoryBonus = "獎金"
    override val categoryRewards = "回饋"

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
    // Expense
    override val categoryBreakfast = "Breakfast"
    override val categoryLunch = "Lunch"
    override val categoryDinner = "Dinner"
    override val categoryDrink = "Drink"
    override val categorySnack = "Snack"
    override val categoryTraffic = "Traffic"
    override val categoryShopping = "Shopping"
    override val categoryDaily = "Daily"
    override val categoryEntertainment = "Fun"
    override val categoryRent = "Rent"
    override val categoryBills = "Bills"
    override val categoryOther = "Other"
    // Income
    override val categorySalary = "Salary"
    override val categoryBonus = "Bonus"
    override val categoryRewards = "Rewards"

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

data class CustomCategory(val name: String, val key: String)

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
    var userAvatar by mutableStateOf("")
        private set

    var isLoggedIn by mutableStateOf(false)
        private set

    var currentUserId by mutableIntStateOf(-1)
        private set

    val currentStrings: StringResources
        get() = if (language == "English") StringsEN else StringsZH

    private val _transactions = mutableStateListOf<Transaction>()
    val transactions: List<Transaction> get() = _transactions

    var customCategories = mutableStateListOf<CustomCategory>()
        private set

    // --- Chart Screen States ---
    var chartYear by mutableIntStateOf(Calendar.getInstance().get(Calendar.YEAR))
    var chartMonth by mutableIntStateOf(Calendar.getInstance().get(Calendar.MONTH) + 1)
    var chartTab by mutableIntStateOf(0)
    var chartTimeMode by mutableIntStateOf(0)
    var customStartDateMillis by mutableLongStateOf(System.currentTimeMillis())
    var customEndDateMillis by mutableLongStateOf(System.currentTimeMillis())

    var chartDataPoints by mutableStateOf<List<Pair<String, Int>>>(emptyList())
        private set
    var averageDaily by mutableIntStateOf(0)
        private set
    var monthlyCategoryStats by mutableStateOf<List<CategoryStat>>(emptyList())
        private set
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

        val c = Calendar.getInstance()
        c.set(Calendar.DAY_OF_MONTH, 1)
        customStartDateMillis = c.timeInMillis
    }

    // 更新大頭照
    fun updateUserAvatar(uri: Uri) {
        if (currentUserId == -1) return

        try {
            // 1. 將圖片複製到 App 內部儲存空間
            val inputStream = context.contentResolver.openInputStream(uri)
            val fileName = "avatar_${currentUserId}_${System.currentTimeMillis()}.jpg"
            val file = File(context.filesDir, fileName)
            inputStream?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            val newPath = file.absolutePath

            // 2. 更新資料庫
            dbHandler.updateUserAvatar(currentUserId, newPath)

            // 3. 更新 Prefs 和 State
            prefs.edit().putString("user_avatar", newPath).apply()
            userAvatar = newPath
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ★ 新增：修改使用者名稱
    fun updateUserName(newName: String): String {
        if (currentUserId == -1) return "尚未登入"
        if (newName.isBlank()) return "名稱不能為空"
        // 如果跟原本的一樣，就直接回傳成功，不用查 DB
        if (newName == userName) return ""

        // 檢查是否重複
        if (dbHandler.checkUserExists(newName)) {
            return "此名稱已被使用"
        }

        // 更新 DB
        dbHandler.updateUserName(currentUserId, newName)

        // 更新 State & Prefs
        userName = newName
        prefs.edit().putString("user_name", newName).apply()

        return "" // 回傳空字串代表成功
    }

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
        val custom = customCategories.find { it.key == key }
        if (custom != null) return custom.name

        val s = currentStrings
        return when (key) {
            "breakfast" -> s.categoryBreakfast
            "lunch" -> s.categoryLunch
            "dinner" -> s.categoryDinner
            "drink" -> s.categoryDrink
            "snack" -> s.categorySnack
            "traffic" -> s.categoryTraffic
            "shopping" -> s.categoryShopping
            "daily" -> s.categoryDaily
            "entertainment" -> s.categoryEntertainment
            "rent" -> s.categoryRent
            "bills" -> s.categoryBills
            "other" -> s.categoryOther
            "salary" -> s.categorySalary
            "bonus" -> s.categoryBonus
            "rewards" -> s.categoryRewards
            "add" -> s.categoryAdd
            else -> key
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
                .putString("user_avatar", "") // 註冊時預設無大頭照
                .putBoolean("is_logged_in", true)
                .apply()

            currentUserId = newUserId.toInt()
            userName = name
            userEmail = email
            userAvatar = ""
            isLoggedIn = true
            _transactions.clear()
            loadCategories()
            return ""
        } else {
            return "註冊失敗，資料庫錯誤"
        }
    }

    fun login(email: String, pass: String): Boolean {
        val userInfo = dbHandler.validateUser(email, pass)

        if (userInfo != null) {
            val (id, name) = userInfo
            // 登入成功後，從 DB 讀取大頭照路徑
            val avatarPath = dbHandler.getUserAvatar(id)

            prefs.edit()
                .putInt("user_id", id)
                .putString("user_name", name)
                .putString("user_email", email)
                .putString("user_avatar", avatarPath)
                .putBoolean("is_logged_in", true)
                .apply()

            currentUserId = id
            userName = name
            userEmail = email
            userAvatar = avatarPath
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
            userAvatar = prefs.getString("user_avatar", "") ?: ""
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
        userAvatar = "" // 清空
        currentUserId = -1
        _transactions.clear()
        customCategories.clear()
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

    fun loadCategories() {
        if (currentUserId == -1) return
        val list = dbHandler.getCategories(currentUserId)
        customCategories.clear()
        list.forEach {
            customCategories.add(CustomCategory(it.first, it.second))
        }
    }

    fun addCustomCategory(name: String): String {
        if (currentUserId == -1) return ""
        val key = "custom_${System.currentTimeMillis()}"
        dbHandler.addCategory(currentUserId, name, key)
        loadCategories()
        return key
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
            loadCategories()

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

    fun loadMonthlyChartData() {
        if (currentUserId == -1) return

        var newChartData = mutableListOf<Pair<String, Int>>()
        var totalSum = 0
        var rawCategoryStats: List<Triple<String, Int, Int>> = emptyList()

        if (chartTab == 2) {
            if (chartTimeMode == 1) {
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
            averageDaily = if (newChartData.isNotEmpty()) totalSum / newChartData.size else 0
            monthlyTotal = totalSum
        } else {
            val typeFilter = if (chartTab == 1) "收入" else "支出"
            if (chartTimeMode == 1) {
                val monthlyStats = dbHandler.getYearlyMonthlyStats(currentUserId, chartYear, typeFilter)
                for (m in 1..12) {
                    val amount = monthlyStats[m] ?: 0
                    newChartData.add(Pair("$m", amount))
                }
                totalSum = monthlyStats.values.sum()
                rawCategoryStats = dbHandler.getYearlyCategoryStats(currentUserId, chartYear, typeFilter)
            } else if (chartTimeMode == 2) {
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
        val statsTotal = rawCategoryStats.sumOf { it.second }
        monthlyCategoryStats = rawCategoryStats.map { (key, sum, count) ->
            val pct = if (statsTotal > 0) (sum.toFloat() / statsTotal) * 100 else 0f
            CategoryStat(key, sum, count, pct)
        }
    }

    fun getTransactionsForCategoryDetail(categoryKey: String): List<Transaction> {
        if (currentUserId == -1) return emptyList()
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