package com.example.accountbook.data // 建議放在 data package 下

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.accountbook.viewmodel.Transaction

class DBHandler(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private const val DB_NAME = "accountbook_db"
        private const val DB_VERSION = 1
        private const val TABLE_NAME = "transactions"

        // 定義欄位名稱
        private const val ID_COL = "id"
        private const val DATE_COL = "date"
        private const val DAY_COL = "day"
        private const val TITLE_COL = "title"
        private const val AMOUNT_COL = "amount"
        private const val TYPE_COL = "type"
        private const val CATEGORY_KEY_COL = "category_key"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // 建立資料表的 SQL 指令
        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " // ID 自動遞增
                + DATE_COL + " TEXT,"
                + DAY_COL + " TEXT,"
                + TITLE_COL + " TEXT,"
                + AMOUNT_COL + " INTEGER,"
                + TYPE_COL + " TEXT,"
                + CATEGORY_KEY_COL + " TEXT)")
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // 資料庫版本更新時，先刪除舊表再重建 (簡單處理)
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // 新增一筆交易
    fun addTransaction(
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

        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    // 讀取所有交易資料
    fun getAllTransactions(): ArrayList<Transaction> {
        val db = this.readableDatabase
        val list = ArrayList<Transaction>()

        // 依照 ID 倒序排列 (最新的在最上面)
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME ORDER BY $ID_COL DESC", null)

        if (cursor.moveToFirst()) {
            do {
                // 注意：這裡的 index 需對應 onCreate 的欄位順序
                val id = cursor.getLong(0)
                val date = cursor.getString(1)
                val day = cursor.getString(2)
                val title = cursor.getString(3)
                val amount = cursor.getInt(4)
                val type = cursor.getString(5)
                val categoryKey = cursor.getString(6)

                list.add(
                    Transaction(
                        id = id,
                        date = date,
                        day = day,
                        title = title,
                        amount = amount,
                        type = type,
                        categoryKey = categoryKey
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    // (選用) 刪除功能
    fun deleteTransaction(id: Long) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "$ID_COL=?", arrayOf(id.toString()))
        db.close()
    }
}