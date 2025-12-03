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
        private const val DB_VERSION = 1
        private const val TABLE_NAME = "transactions"

        private const val ID_COL = "id"
        private const val DATE_COL = "date"
        private const val DAY_COL = "day"
        private const val TITLE_COL = "title"
        private const val AMOUNT_COL = "amount"
        private const val TYPE_COL = "type"
        private const val CATEGORY_KEY_COL = "category_key"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DATE_COL + " TEXT,"
                + DAY_COL + " TEXT,"
                + TITLE_COL + " TEXT,"
                + AMOUNT_COL + " INTEGER,"
                + TYPE_COL + " TEXT,"
                + CATEGORY_KEY_COL + " TEXT)")
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // 新增交易
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

    // 取得所有交易
    fun getAllTransactions(): ArrayList<Transaction> {
        val db = this.readableDatabase
        val list = ArrayList<Transaction>()

        // 依照 ID 倒序 (最新的在最上面)
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME ORDER BY $ID_COL DESC", null)

        if (cursor.moveToFirst()) {
            do {
                list.add(
                    Transaction(
                        id = cursor.getLong(0),
                        date = cursor.getString(1),
                        day = cursor.getString(2),
                        title = cursor.getString(3),
                        amount = cursor.getInt(4),
                        type = cursor.getString(5),
                        categoryKey = cursor.getString(6)
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    // 修改交易
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

    // 刪除交易
    fun deleteTransaction(id: Long) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "$ID_COL=?", arrayOf(id.toString()))
        db.close()
    }

    // 根據 ID 取得單筆資料 (編輯回填用)
    fun getTransactionById(id: Long): Transaction? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $ID_COL=?", arrayOf(id.toString()))
        var transaction: Transaction? = null
        if (cursor.moveToFirst()) {
            transaction = Transaction(
                id = cursor.getLong(0),
                date = cursor.getString(1),
                day = cursor.getString(2),
                title = cursor.getString(3),
                amount = cursor.getInt(4),
                type = cursor.getString(5),
                categoryKey = cursor.getString(6)
            )
        }
        cursor.close()
        return transaction
    }
}