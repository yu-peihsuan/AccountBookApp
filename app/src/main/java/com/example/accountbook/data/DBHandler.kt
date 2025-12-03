package com.example.accountbook.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.accountbook.viewmodel.Transaction

class DBHandler(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private const val DB_NAME = "accountbook_db"
        // ★ 修改：版本號升級為 3 (因為修改了資料表結構)
        private const val DB_VERSION = 3

        // --- 交易資料表 ---
        private const val TABLE_NAME = "transactions"
        private const val ID_COL = "id"
        // ★ 新增：使用者 ID 欄位 (Foreign Key 概念)
        private const val TR_USER_ID_COL = "user_id"
        private const val DATE_COL = "date"
        private const val DAY_COL = "day"
        private const val TITLE_COL = "title"
        private const val AMOUNT_COL = "amount"
        private const val TYPE_COL = "type"
        private const val CATEGORY_KEY_COL = "category_key"

        // --- 使用者資料表 ---
        private const val TABLE_USERS = "users"
        private const val USER_ID_COL = "id"
        private const val USER_NAME_COL = "name"
        private const val USER_EMAIL_COL = "email"
        private const val USER_PASS_COL = "password"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // ★ 修改：建立交易表時，加入 user_id 欄位
        val queryTransaction = ("CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TR_USER_ID_COL + " INTEGER,"  // 儲存這筆交易屬於誰
                + DATE_COL + " TEXT,"
                + DAY_COL + " TEXT,"
                + TITLE_COL + " TEXT,"
                + AMOUNT_COL + " INTEGER,"
                + TYPE_COL + " TEXT,"
                + CATEGORY_KEY_COL + " TEXT)")
        db.execSQL(queryTransaction)

        // 建立使用者表 (保持不變)
        val queryUsers = ("CREATE TABLE " + TABLE_USERS + " ("
                + USER_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + USER_NAME_COL + " TEXT,"
                + USER_EMAIL_COL + " TEXT,"
                + USER_PASS_COL + " TEXT)")
        db.execSQL(queryUsers)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // 簡單處理：升級時刪除舊表重建 (注意：這會清空舊資料)
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    // ---------------- 使用者相關功能 ----------------

    // 檢查使用者名稱是否已存在
    fun checkUserExists(name: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USERS WHERE $USER_NAME_COL = ?", arrayOf(name))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    // ★ 新增：檢查 Email 是否已存在
    fun checkEmailExists(email: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USERS WHERE $USER_EMAIL_COL = ?", arrayOf(email))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    // ★ 修改：註冊新使用者 (回傳新生成的 ID)
    fun addUser(name: String, email: String, pass: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(USER_NAME_COL, name)
        values.put(USER_EMAIL_COL, email)
        values.put(USER_PASS_COL, pass)

        // insert 會回傳新資料的 row ID
        val newId = db.insert(TABLE_USERS, null, values)
        db.close()
        return newId
    }

    // ★ 修改：登入驗證 (回傳 ID 和 Name 的 Pair，失敗回傳 null)
    fun validateUser(email: String, pass: String): Pair<Int, String>? {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_USERS WHERE $USER_EMAIL_COL = ? AND $USER_PASS_COL = ?",
            arrayOf(email, pass)
        )

        var userInfo: Pair<Int, String>? = null
        if (cursor.moveToFirst()) {
            // index 0: id, index 1: name
            val id = cursor.getInt(0)
            val name = cursor.getString(1)
            userInfo = Pair(id, name)
        }
        cursor.close()
        return userInfo
    }

    // ---------------- 交易相關功能 (加上 User ID 過濾) ----------------

    // ★ 修改：新增交易時，必須傳入 userId
    fun addTransaction(
        userId: Int,
        date: String,
        day: String,
        title: String,
        amount: Int,
        type: String,
        categoryKey: String
    ) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(TR_USER_ID_COL, userId) // 寫入擁有者 ID
        values.put(DATE_COL, date)
        values.put(DAY_COL, day)
        values.put(TITLE_COL, title)
        values.put(AMOUNT_COL, amount)
        values.put(TYPE_COL, type)
        values.put(CATEGORY_KEY_COL, categoryKey)
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    // ★ 修改：讀取交易時，必須傳入 userId 來篩選
    fun getAllTransactions(userId: Int): ArrayList<Transaction> {
        val db = this.readableDatabase
        val list = ArrayList<Transaction>()

        // 只選取該 userId 的資料
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM $TABLE_NAME WHERE $TR_USER_ID_COL = ? ORDER BY $ID_COL DESC",
            arrayOf(userId.toString())
        )

        if (cursor.moveToFirst()) {
            do {
                // 注意 index 變化：
                // 0: id, 1: user_id, 2: date, 3: day, 4: title, 5: amount, 6: type, 7: category_key
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

    // 修改交易 (維持原樣，根據 Transaction ID 修改即可)
    fun updateTransaction(
        id: Long,
        date: String,
        day: String,
        title: String,
        amount: Int,
        type: String,
        categoryKey: String
    ) {
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

    // 刪除交易 (維持原樣)
    fun deleteTransaction(id: Long) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "$ID_COL=?", arrayOf(id.toString()))
        db.close()
    }

    // 取得單筆資料 (維持原樣)
    fun getTransactionById(id: Long): Transaction? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $ID_COL=?", arrayOf(id.toString()))
        var transaction: Transaction? = null
        if (cursor.moveToFirst()) {
            // 記得 index 對應
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
}