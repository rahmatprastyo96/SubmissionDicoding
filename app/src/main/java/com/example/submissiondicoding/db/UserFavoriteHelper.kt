package com.example.submissiondicoding.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import com.example.submissiondicoding.db.DatabaseContract.UserColumns.Companion.ID
import com.example.submissiondicoding.db.DatabaseContract.UserColumns.Companion.TABLE_NAME
import com.example.submissiondicoding.db.DatabaseContract.UserColumns.Companion.USERNAME

class UserFavoriteHelper(context: Context) {

    private var dataBaseHelper: DatabaseHelper = DatabaseHelper(context)
    private lateinit var database: SQLiteDatabase

    companion object {
        private const val DATABASE_TABLE = TABLE_NAME

        private var INSTANCE: UserFavoriteHelper? = null
        fun getInstance(context: Context): UserFavoriteHelper =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserFavoriteHelper(context)
            }
    }

    @Throws(SQLException::class)
    fun open() {
        database = dataBaseHelper.writableDatabase
    }

    fun close() {
        dataBaseHelper.close()
        if (database.isOpen)
            database.close()
    }

    fun queryAll(): Cursor {
        return database.query(
            DATABASE_TABLE,
            null,
            null,
            null,
            null,
            null,
            "$ID ASC")
    }

    fun queryByUsername(username: String) : Cursor {
        return database.query(
            DATABASE_TABLE,
            null,
            "$USERNAME = ?",
            arrayOf(username),
            null,
            null,
            null,
            null)
    }

    fun insert(values: ContentValues?): Long {
        return database.insert(DATABASE_TABLE, null, values)
    }

    fun deleteById(id: String): Int {
        return database.delete(DATABASE_TABLE, "$ID = '$id'", null)
    }
}