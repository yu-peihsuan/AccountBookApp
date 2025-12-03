package com.example.accountbook.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.accountbook.data.DBHandler
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Locale

// ---------------- 多語系資源定義 (保持原樣) ----------------
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
    override val chartPie = "📈 圓餅圖 (之後串資料)"
    override val chartBar = "📊 長條圖 (之後串資料)"
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
    override val chartPie = "📈 Pie Chart (Coming soon)"
    override val chartBar = "📊 Bar Chart (Coming soon)"
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

    // 使用者狀態
    var userName by mutableStateOf("")
        private set
    var userEmail by mutableStateOf("")
        private set
    var isLoggedIn by mutableStateOf(false)
        private set

    // ★ 新增：目前使用者的 ID (預設 -1 代表未登入)
    var currentUserId by mutableIntStateOf(-1)
        private set

    val currentStrings: StringResources
        get() = if (language == "English") StringsEN else StringsZH

    private val _transactions = mutableStateListOf<Transaction>()
    val transactions: List<Transaction> get() = _transactions

    init {
        loadSettings()
        checkLoginStatus() // 先檢查登入狀態，這會設定 currentUserId
        loadData()         // 再根據 ID 載入資料
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

    // ★ 修改：註冊邏輯
    fun register(name: String, email: String, pass: String): String {
        // 1. 檢查使用者名稱重複
        if (dbHandler.checkUserExists(name)) {
            return "使用者名稱已存在"
        }
        // 2. ★ 新增：檢查 Email 重複
        if (dbHandler.checkEmailExists(email)) {
            return "此 Email 已被註冊"
        }

        // 3. 寫入資料庫，並取得新 ID
        val newUserId = dbHandler.addUser(name, email, pass)

        if (newUserId != -1L) {
            // 4. 註冊成功，自動登入並儲存狀態
            prefs.edit()
                .putInt("user_id", newUserId.toInt()) // 儲存 ID
                .putString("user_name", name)
                .putString("user_email", email)
                .putBoolean("is_logged_in", true)
                .apply()

            currentUserId = newUserId.toInt()
            userName = name
            userEmail = email
            isLoggedIn = true

            // 清空舊資料 (因為剛註冊，一定是空的)
            _transactions.clear()

            return "" // 成功回傳空字串
        } else {
            return "註冊失敗，資料庫錯誤"
        }
    }

    // ★ 修改：登入邏輯
    fun login(email: String, pass: String): Boolean {
        // 取得使用者資訊 (ID, Name)
        val userInfo = dbHandler.validateUser(email, pass)

        if (userInfo != null) {
            val (id, name) = userInfo

            // 儲存登入狀態
            prefs.edit()
                .putInt("user_id", id) // 儲存 ID
                .putString("user_name", name)
                .putString("user_email", email)
                .putBoolean("is_logged_in", true)
                .apply()

            currentUserId = id
            userName = name
            userEmail = email
            isLoggedIn = true

            // ★ 登入後，重新載入該使用者的資料
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
            // 從 prefs 恢復狀態
            currentUserId = prefs.getInt("user_id", -1)
            userName = prefs.getString("user_name", "") ?: ""
            userEmail = prefs.getString("user_email", "") ?: ""
        }
    }

    // ★ 修改：登出邏輯
    fun logout() {
        prefs.edit()
            .putBoolean("is_logged_in", false)
            .remove("user_id") // 清除 ID
            .apply()

        isLoggedIn = false
        userName = ""
        userEmail = ""
        currentUserId = -1

        // 登出後清空記帳列表
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

    // ★ 修改：新增資料時傳入 currentUserId
    fun addTransaction(title: String, amount: Int, type: String, dateMillis: Long, categoryKey: String) {
        if (currentUserId == -1) return // 未登入不處理

        val formatD = SimpleDateFormat(currentStrings.dateFormat, if(language=="English") Locale.US else Locale.TAIWAN)
        val formatW = SimpleDateFormat(currentStrings.dayFormat, if(language=="English") Locale.US else Locale.TAIWAN)

        val dateStr = formatD.format(dateMillis)
        val dayStr = formatW.format(dateMillis)

        dbHandler.addTransaction(currentUserId, dateStr, dayStr, title, amount, type, categoryKey)
        loadData()
    }

    // 更新資料 (不需要 userId，因為 id 是唯一的 PK)
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

    // ★ 修改：根據 currentUserId 載入資料
    private fun loadData() {
        if (currentUserId != -1) {
            val list = dbHandler.getAllTransactions(currentUserId)
            _transactions.clear()
            _transactions.addAll(list)
        } else {
            _transactions.clear()
        }
    }

    private fun loadSettings() {
        budget = prefs.getInt("monthly_budget", 8000)
        currency = prefs.getString("currency_symbol", "NT$") ?: "NT$"
        language = prefs.getString("app_language", "中文(繁體)") ?: "中文(繁體)"
    }
}