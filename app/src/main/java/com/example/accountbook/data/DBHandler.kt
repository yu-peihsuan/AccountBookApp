package com.example.accountbook.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.accountbook.viewmodel.Transaction
import java.util.ArrayList
import java.util.HashMap

class DBHandler(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private const val DB_NAME = "accountbook_db"
        private const val DB_VERSION = 5

        private const val TABLE_NAME = "transactions"
        private const val ID_COL = "id"
        private const val TR_USER_ID_COL = "user_id"
        private const val DATE_COL = "date" // 格式: yyyy/MM/dd
        private const val DAY_COL = "day"
        private const val TITLE_COL = "title"
        private const val AMOUNT_COL = "amount"
        private const val TYPE_COL = "type"
        private const val CATEGORY_KEY_COL = "category_key"

        private const val TABLE_USERS = "users"
        private const val USER_ID_COL = "id"
        private const val USER_NAME_COL = "name"
        private const val USER_EMAIL_COL = "email"
        private const val USER_PASS_COL = "password"
        private const val USER_AVATAR_COL = "avatar"

        // 類別資料表
        private const val TABLE_CATEGORIES = "categories"
        private const val CAT_ID_COL = "id"
        private const val CAT_USER_ID_COL = "user_id"
        private const val CAT_NAME_COL = "name"
        private const val CAT_KEY_COL = "key"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val queryTransaction = ("CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TR_USER_ID_COL + " INTEGER,"
                + DATE_COL + " TEXT,"
                + DAY_COL + " TEXT,"
                + TITLE_COL + " TEXT,"
                + AMOUNT_COL + " INTEGER,"
                + TYPE_COL + " TEXT,"
                + CATEGORY_KEY_COL + " TEXT)")
        db.execSQL(queryTransaction)

        val queryUsers = ("CREATE TABLE " + TABLE_USERS + " ("
                + USER_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + USER_NAME_COL + " TEXT,"
                + USER_EMAIL_COL + " TEXT,"
                + USER_PASS_COL + " TEXT,"
                + USER_AVATAR_COL + " TEXT)")
        db.execSQL(queryUsers)

        // 建立類別資料表
        val queryCategories = ("CREATE TABLE " + TABLE_CATEGORIES + " ("
                + CAT_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CAT_USER_ID_COL + " INTEGER,"
                + CAT_NAME_COL + " TEXT,"
                + CAT_KEY_COL + " TEXT)")
        db.execSQL(queryCategories)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 4) {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_CATEGORIES")
            onCreate(db)
            return
        }

        if (oldVersion < 5) {
            try {
                db.execSQL("ALTER TABLE $TABLE_USERS ADD COLUMN $USER_AVATAR_COL TEXT")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // --- User Methods ---
    fun checkUserExists(name: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USERS WHERE $USER_NAME_COL = ?", arrayOf(name))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun checkEmailExists(email: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USERS WHERE $USER_EMAIL_COL = ?", arrayOf(email))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun addUser(name: String, email: String, pass: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(USER_NAME_COL, name)
        values.put(USER_EMAIL_COL, email)
        values.put(USER_PASS_COL, pass)
        values.put(USER_AVATAR_COL, "") // 預設空字串
        val newId = db.insert(TABLE_USERS, null, values)
        db.close()
        return newId
    }

    fun validateUser(email: String, pass: String): Pair<Int, String>? {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_USERS WHERE $USER_EMAIL_COL = ? AND $USER_PASS_COL = ?",
            arrayOf(email, pass)
        )
        var userInfo: Pair<Int, String>? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(0)
            val name = cursor.getString(1)
            userInfo = Pair(id, name)
        }
        cursor.close()
        return userInfo
    }

    fun updateUserAvatar(userId: Int, path: String) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(USER_AVATAR_COL, path)
        db.update(TABLE_USERS, values, "$USER_ID_COL = ?", arrayOf(userId.toString()))
        db.close()
    }

    fun updateUserName(userId: Int, newName: String) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(USER_NAME_COL, newName)
        db.update(TABLE_USERS, values, "$USER_ID_COL = ?", arrayOf(userId.toString()))
        db.close()
    }

    fun getUserAvatar(userId: Int): String {
        val db = this.readableDatabase
        var avatarPath = ""
        val cursor = db.rawQuery("SELECT $USER_AVATAR_COL FROM $TABLE_USERS WHERE $USER_ID_COL = ?", arrayOf(userId.toString()))
        if (cursor.moveToFirst()) {
            avatarPath = cursor.getString(0) ?: ""
        }
        cursor.close()
        return avatarPath
    }

    // 刪除使用者所有資料 (交易、分類、帳號)
    fun deleteUserData(userId: Int) {
        val db = this.writableDatabase
        db.beginTransaction()
        try {
            // 1. 刪除該使用者的所有交易
            db.delete(TABLE_NAME, "$TR_USER_ID_COL = ?", arrayOf(userId.toString()))
            // 2. 刪除該使用者的自訂分類
            db.delete(TABLE_CATEGORIES, "$CAT_USER_ID_COL = ?", arrayOf(userId.toString()))
            // 3. 刪除使用者帳號
            db.delete(TABLE_USERS, "$USER_ID_COL = ?", arrayOf(userId.toString()))

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    // --- Category Methods ---
    fun addCategory(userId: Int, name: String, key: String) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(CAT_USER_ID_COL, userId)
        values.put(CAT_NAME_COL, name)
        values.put(CAT_KEY_COL, key)
        db.insert(TABLE_CATEGORIES, null, values)
        db.close()
    }

    fun getCategories(userId: Int): List<Pair<String, String>> {
        val list = ArrayList<Pair<String, String>>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_CATEGORIES WHERE $CAT_USER_ID_COL = ?", arrayOf(userId.toString()))
        if (cursor.moveToFirst()) {
            do {
                val name = cursor.getString(2) // name column index
                val key = cursor.getString(3)  // key column index
                list.add(Pair(name, key))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    // --- Transaction Methods ---
    fun addTransaction(userId: Int, date: String, day: String, title: String, amount: Int, type: String, categoryKey: String) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(TR_USER_ID_COL, userId)
        values.put(DATE_COL, date)
        values.put(DAY_COL, day)
        values.put(TITLE_COL, title)
        values.put(AMOUNT_COL, amount)
        values.put(TYPE_COL, type)
        values.put(CATEGORY_KEY_COL, categoryKey)
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun updateTransaction(id: Long, date: String, day: String, title: String, amount: Int, type: String, categoryKey: String) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(DATE_COL, date)
        values.put(DAY_COL, day)
        values.put(TITLE_COL, title)
        values.put(AMOUNT_COL, amount)
        values.put(TYPE_COL, type)
        values.put(CATEGORY_KEY_COL, categoryKey)
        db.update(TABLE_NAME, values, "$ID_COL=?", arrayOf(id.toString()))
        db.close()
    }

    fun deleteTransaction(id: Long) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "$ID_COL=?", arrayOf(id.toString()))
        db.close()
    }

    fun getTransactionById(id: Long): Transaction? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $ID_COL=?", arrayOf(id.toString()))
        var transaction: Transaction? = null
        if (cursor.moveToFirst()) {
            transaction = Transaction(
                id = cursor.getLong(0),
                date = cursor.getString(2),
                day = cursor.getString(3),
                title = cursor.getString(4),
                amount = cursor.getInt(5),
                type = cursor.getString(6),
                categoryKey = cursor.getString(7)
            )
        }
        cursor.close()
        return transaction
    }

    fun getAllTransactions(userId: Int): ArrayList<Transaction> {
        val db = this.readableDatabase
        val list = ArrayList<Transaction>()
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM $TABLE_NAME WHERE $TR_USER_ID_COL = ? ORDER BY $DATE_COL DESC, $ID_COL DESC",
            arrayOf(userId.toString())
        )
        if (cursor.moveToFirst()) {
            do {
                list.add(
                    Transaction(
                        id = cursor.getLong(0),
                        date = cursor.getString(2),
                        day = cursor.getString(3),
                        title = cursor.getString(4),
                        amount = cursor.getInt(5),
                        type = cursor.getString(6),
                        categoryKey = cursor.getString(7)
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    // --- 月檢視：取得特定月份的每日金額 ---
    fun getMonthlyDailyStats(userId: Int, year: Int, month: Int, typeFilter: String): Map<Int, Int> {
        val db = this.readableDatabase
        val map = HashMap<Int, Int>()

        val monthStr = String.format("%04d/%02d", year, month) + "%"
        val isExpense = typeFilter == "支出" || typeFilter == "Expense"
        val typeQuery = if (isExpense) "($TYPE_COL = '支出' OR $TYPE_COL = 'Expense')" else "($TYPE_COL = '收入' OR $TYPE_COL = 'Income')"

        val cursor = db.rawQuery(
            "SELECT $DATE_COL, SUM($AMOUNT_COL) FROM $TABLE_NAME " +
                    "WHERE $TR_USER_ID_COL = ? AND $typeQuery AND $DATE_COL LIKE ? " +
                    "GROUP BY $DATE_COL",
            arrayOf(userId.toString(), monthStr)
        )

        if (cursor.moveToFirst()) {
            do {
                val date = cursor.getString(0)
                val sum = cursor.getInt(1)
                val parts = date.split("/")
                if (parts.size == 3) {
                    val day = parts[2].toIntOrNull() ?: 0
                    map[day] = sum
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
        return map
    }

    // --- 月檢視：取得特定月份的分類統計 ---
    fun getMonthlyCategoryStats(userId: Int, year: Int, month: Int, typeFilter: String): List<Triple<String, Int, Int>> {
        val db = this.readableDatabase
        val list = ArrayList<Triple<String, Int, Int>>()

        val monthStr = String.format("%04d/%02d", year, month) + "%"
        val isExpense = typeFilter == "支出" || typeFilter == "Expense"
        val typeQuery = if (isExpense) "($TYPE_COL = '支出' OR $TYPE_COL = 'Expense')" else "($TYPE_COL = '收入' OR $TYPE_COL = 'Income')"

        val cursor = db.rawQuery(
            "SELECT $CATEGORY_KEY_COL, SUM($AMOUNT_COL), COUNT($ID_COL) FROM $TABLE_NAME " +
                    "WHERE $TR_USER_ID_COL = ? AND $typeQuery AND $DATE_COL LIKE ? " +
                    "GROUP BY $CATEGORY_KEY_COL ORDER BY SUM($AMOUNT_COL) DESC",
            arrayOf(userId.toString(), monthStr)
        )

        if (cursor.moveToFirst()) {
            do {
                val key = cursor.getString(0) ?: "other"
                val sum = cursor.getInt(1)
                val count = cursor.getInt(2)
                list.add(Triple(key, sum, count))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    // --- 年檢視：取得特定年份的每月金額 ---
    fun getYearlyMonthlyStats(userId: Int, year: Int, typeFilter: String): Map<Int, Int> {
        val db = this.readableDatabase
        val map = HashMap<Int, Int>()
        val yearStr = "$year/%"
        val isExpense = typeFilter == "支出" || typeFilter == "Expense"
        val typeQuery = if (isExpense) "($TYPE_COL = '支出' OR $TYPE_COL = 'Expense')" else "($TYPE_COL = '收入' OR $TYPE_COL = 'Income')"

        val cursor = db.rawQuery(
            "SELECT $DATE_COL, SUM($AMOUNT_COL) FROM $TABLE_NAME " +
                    "WHERE $TR_USER_ID_COL = ? AND $typeQuery AND $DATE_COL LIKE ? " +
                    "GROUP BY $DATE_COL",
            arrayOf(userId.toString(), yearStr)
        )

        if (cursor.moveToFirst()) {
            do {
                val date = cursor.getString(0)
                val sum = cursor.getInt(1)
                val parts = date.split("/")
                if (parts.size >= 2) {
                    val m = parts[1].toIntOrNull() ?: 0
                    map[m] = map.getOrDefault(m, 0) + sum
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
        return map
    }

    // --- 年檢視：取得特定年份的分類統計 ---
    fun getYearlyCategoryStats(userId: Int, year: Int, typeFilter: String): List<Triple<String, Int, Int>> {
        val db = this.readableDatabase
        val list = ArrayList<Triple<String, Int, Int>>()
        val yearStr = "$year/%"
        val isExpense = typeFilter == "支出" || typeFilter == "Expense"
        val typeQuery = if (isExpense) "($TYPE_COL = '支出' OR $TYPE_COL = 'Expense')" else "($TYPE_COL = '收入' OR $TYPE_COL = 'Income')"

        val cursor = db.rawQuery(
            "SELECT $CATEGORY_KEY_COL, SUM($AMOUNT_COL), COUNT($ID_COL) FROM $TABLE_NAME " +
                    "WHERE $TR_USER_ID_COL = ? AND $typeQuery AND $DATE_COL LIKE ? " +
                    "GROUP BY $CATEGORY_KEY_COL ORDER BY SUM($AMOUNT_COL) DESC",
            arrayOf(userId.toString(), yearStr)
        )

        if (cursor.moveToFirst()) {
            do {
                val key = cursor.getString(0) ?: "other"
                val sum = cursor.getInt(1)
                val count = cursor.getInt(2)
                list.add(Triple(key, sum, count))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    // --- 自訂區間：取得區間內的每日金額 ---
    fun getRangeDailyStats(userId: Int, startDate: String, endDate: String, typeFilter: String): Map<String, Int> {
        val db = this.readableDatabase
        val map = HashMap<String, Int>()

        val isExpense = typeFilter == "支出" || typeFilter == "Expense"
        val typeQuery = if (isExpense) "($TYPE_COL = '支出' OR $TYPE_COL = 'Expense')" else "($TYPE_COL = '收入' OR $TYPE_COL = 'Income')"

        val cursor = db.rawQuery(
            "SELECT $DATE_COL, SUM($AMOUNT_COL) FROM $TABLE_NAME " +
                    "WHERE $TR_USER_ID_COL = ? AND $typeQuery AND $DATE_COL >= ? AND $DATE_COL <= ? " +
                    "GROUP BY $DATE_COL",
            arrayOf(userId.toString(), startDate, endDate)
        )

        if (cursor.moveToFirst()) {
            do {
                val date = cursor.getString(0)
                val sum = cursor.getInt(1)
                map[date] = sum
            } while (cursor.moveToNext())
        }
        cursor.close()
        return map
    }

    // --- 自訂區間：取得區間內的分類統計 ---
    fun getRangeCategoryStats(userId: Int, startDate: String, endDate: String, typeFilter: String): List<Triple<String, Int, Int>> {
        val db = this.readableDatabase
        val list = ArrayList<Triple<String, Int, Int>>()

        val isExpense = typeFilter == "支出" || typeFilter == "Expense"
        val typeQuery = if (isExpense) "($TYPE_COL = '支出' OR $TYPE_COL = 'Expense')" else "($TYPE_COL = '收入' OR $TYPE_COL = 'Income')"

        val cursor = db.rawQuery(
            "SELECT $CATEGORY_KEY_COL, SUM($AMOUNT_COL), COUNT($ID_COL) FROM $TABLE_NAME " +
                    "WHERE $TR_USER_ID_COL = ? AND $typeQuery AND $DATE_COL >= ? AND $DATE_COL <= ? " +
                    "GROUP BY $CATEGORY_KEY_COL ORDER BY SUM($AMOUNT_COL) DESC",
            arrayOf(userId.toString(), startDate, endDate)
        )

        if (cursor.moveToFirst()) {
            do {
                val key = cursor.getString(0) ?: "other"
                val sum = cursor.getInt(1)
                val count = cursor.getInt(2)
                list.add(Triple(key, sum, count))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    // 取得特定分類在指定範圍內的詳細交易列表
    fun getCategoryTransactions(userId: Int, categoryKey: String, startDate: String, endDate: String, typeFilter: String, isLikeQuery: Boolean): List<Transaction> {
        val db = this.readableDatabase
        val list = ArrayList<Transaction>()

        val isExpense = typeFilter == "支出" || typeFilter == "Expense"
        val typeQuery = if (isExpense) "($TYPE_COL = '支出' OR $TYPE_COL = 'Expense')" else "($TYPE_COL = '收入' OR $TYPE_COL = 'Income')"

        // 構建查詢語句
        val dateCondition = if (isLikeQuery) "$DATE_COL LIKE ?" else "$DATE_COL >= ? AND $DATE_COL <= ?"
        val args = if (isLikeQuery) {
            arrayOf(userId.toString(), categoryKey, startDate)
        } else {
            arrayOf(userId.toString(), categoryKey, startDate, endDate)
        }

        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_NAME " +
                    "WHERE $TR_USER_ID_COL = ? AND $CATEGORY_KEY_COL = ? AND $typeQuery AND $dateCondition " +
                    "ORDER BY $DATE_COL DESC, $ID_COL DESC",
            args
        )

        if (cursor.moveToFirst()) {
            do {
                list.add(
                    Transaction(
                        id = cursor.getLong(0),
                        date = cursor.getString(2),
                        day = cursor.getString(3),
                        title = cursor.getString(4),
                        amount = cursor.getInt(5),
                        type = cursor.getString(6),
                        categoryKey = cursor.getString(7)
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    // 首頁圓餅圖用
    fun getCategoryTotals(userId: Int): Map<String, Int> {
        val db = this.readableDatabase
        val map = HashMap<String, Int>()
        val cursor = db.rawQuery(
            "SELECT $CATEGORY_KEY_COL, SUM($AMOUNT_COL) FROM $TABLE_NAME " +
                    "WHERE $TR_USER_ID_COL = ? AND ($TYPE_COL = '支出' OR $TYPE_COL = 'Expense') " +
                    "GROUP BY $CATEGORY_KEY_COL",
            arrayOf(userId.toString())
        )
        if (cursor.moveToFirst()) {
            do {
                val cat = cursor.getString(0) ?: "other"
                val sum = cursor.getInt(1)
                map[cat] = sum
            } while (cursor.moveToNext())
        }
        cursor.close()
        return map
    }

    // 首頁長條圖用
    fun getRecentDailyTotals(userId: Int): List<Pair<String, Int>> {
        val db = this.readableDatabase
        val list = ArrayList<Pair<String, Int>>()
        val cursor = db.rawQuery(
            "SELECT $DATE_COL, SUM($AMOUNT_COL) FROM $TABLE_NAME " +
                    "WHERE $TR_USER_ID_COL = ? AND ($TYPE_COL = '支出' OR $TYPE_COL = 'Expense') " +
                    "GROUP BY $DATE_COL ORDER BY $DATE_COL DESC LIMIT 7",
            arrayOf(userId.toString())
        )
        if (cursor.moveToFirst()) {
            do {
                val date = cursor.getString(0)
                val sum = cursor.getInt(1)
                list.add(Pair(date, sum))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list.reversed()
    }
}