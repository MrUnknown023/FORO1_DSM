package com.sv.udb.registronotasapp.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.sv.udb.registronotasapp.data.db.DbStruct.UsersTable;
import com.sv.udb.registronotasapp.data.db.DbStruct.SubjectsTable;
import com.sv.udb.registronotasapp.data.db.DbStruct.ActivitiesTable;


class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DbStruct.DATABASE_NAME,
    null,
    DbStruct.DATABASE_VERSION
) {

    override fun onCreate(db: SQLiteDatabase) {
        val createUsersTable = """
            CREATE TABLE ${UsersTable.TABLE_NAME} (
                ${UsersTable.COL_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${UsersTable.COL_EMAIL} TEXT UNIQUE NOT NULL,
                ${UsersTable.COL_PASSWORD} TEXT NOT NULL,
                ${UsersTable.COL_DISPLAY_NAME} TEXT NOT NULL
            )
        """.trimIndent()

        val createSubjectsTable = """
            CREATE TABLE ${SubjectsTable.TABLE_NAME} (
                ${SubjectsTable.COL_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${SubjectsTable.COL_USER_ID} INTEGER NOT NULL,
                ${SubjectsTable.COL_NAME} TEXT NOT NULL,
                ${SubjectsTable.COL_CREATED_AT} TEXT NOT NULL
            )
        """.trimIndent()

        val createActivitiesTable = """
            CREATE TABLE ${ActivitiesTable.TABLE_NAME} (
                ${ActivitiesTable.COL_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${ActivitiesTable.COL_SUBJECT_ID} INTEGER NOT NULL,
                ${ActivitiesTable.COL_NAME} TEXT NOT NULL,
                ${ActivitiesTable.COL_PERCENTAGE} REAL NOT NULL,
                ${ActivitiesTable.COL_SCORE} REAL NOT NULL DEFAULT 0,
                ${ActivitiesTable.COL_CREATED_AT} TEXT NOT NULL
            )
        """.trimIndent()

        db.execSQL(createUsersTable)
        db.execSQL(createSubjectsTable)
        db.execSQL(createActivitiesTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${ActivitiesTable.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${SubjectsTable.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${UsersTable.TABLE_NAME}")
        onCreate(db)
    }
}