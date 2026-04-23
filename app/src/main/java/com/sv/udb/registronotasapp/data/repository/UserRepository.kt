package com.sv.udb.registronotasapp.data.repository

import android.content.ContentValues
import android.content.Context
import com.sv.udb.registronotasapp.data.db.DatabaseHelper
import com.sv.udb.registronotasapp.data.db.DbStruct.UsersTable
import com.sv.udb.registronotasapp.data.model.User

class UserRepository (context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun registerUser(email: String, password: String, displayName: String): Result<Long> {
        return try {
            val db = dbHelper.writableDatabase

            val values = ContentValues().apply {
                put(UsersTable.COL_EMAIL, email)
                put(UsersTable.COL_PASSWORD, password)
                put(UsersTable.COL_DISPLAY_NAME, displayName)
            }

            val id = db.insertOrThrow(UsersTable.TABLE_NAME, null, values)
            db.close()
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun loginUser(email: String, password: String): User? {
        val db = dbHelper.readableDatabase

        val cursor = db.query(
            UsersTable.TABLE_NAME,
            null,
            "${UsersTable.COL_EMAIL} = ? AND ${UsersTable.COL_PASSWORD} = ?",
            arrayOf(email, password),
            null,
            null,
            null
        )

        val user = if (cursor.moveToFirst()) {
            User(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(UsersTable.COL_ID)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(UsersTable.COL_EMAIL)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(UsersTable.COL_PASSWORD)),
                displayName = cursor.getString(cursor.getColumnIndexOrThrow(UsersTable.COL_DISPLAY_NAME))
            )
        } else {
            null
        }

        cursor.close()
        db.close()
        return user
    }

    fun userExists(email: String): Boolean {
        val db = dbHelper.readableDatabase

        val cursor = db.query(
            UsersTable.TABLE_NAME,
            arrayOf(UsersTable.COL_ID),
            "${UsersTable.COL_EMAIL} = ?",
            arrayOf(email),
            null,
            null,
            null
        )

        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    fun getUserByEmail(email: String): User? {
        val db = dbHelper.readableDatabase

        val cursor = db.query(
            UsersTable.TABLE_NAME,
            null,
            "${UsersTable.COL_EMAIL} = ?",
            arrayOf(email),
            null,
            null,
            null
        )

        val user = if (cursor.moveToFirst()) {
            User(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(UsersTable.COL_ID)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(UsersTable.COL_EMAIL)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(UsersTable.COL_PASSWORD)),
                displayName = cursor.getString(cursor.getColumnIndexOrThrow(UsersTable.COL_DISPLAY_NAME))
            )
        } else {
            null
        }

        cursor.close()
        db.close()
        return user
    }
}