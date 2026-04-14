package com.sv.udb.registronotasapp.data.repository

import android.content.ContentValues
import android.content.Context
import com.sv.udb.registronotasapp.data.db.DatabaseHelper
import com.sv.udb.registronotasapp.data.db.DbStruct
import com.sv.udb.registronotasapp.data.db.DbStruct.SubjectsTable;
import com.sv.udb.registronotasapp.data.model.Subject

class SubjectRepository (context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun insertSubject(subject: Subject): Long {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DbStruct.SubjectsTable.COL_USER_ID, subject.userId)
            put(SubjectsTable.COL_NAME, subject.name)
            put(SubjectsTable.COL_CREATED_AT, subject.createdAt)
        }

        val id = db.insert(SubjectsTable.TABLE_NAME, null, values)
        db.close()
        return id
    }

    fun getSubjectsByUser(userId: Long): List<Subject> {
        val db = dbHelper.readableDatabase

        val cursor = db.query(
            SubjectsTable.TABLE_NAME,
            null,
            "${SubjectsTable.COL_USER_ID} = ?",
            arrayOf(userId.toString()),
            null,
            null,
            "${SubjectsTable.COL_ID} DESC"
        )

        val subjects = mutableListOf<Subject>()

        while (cursor.moveToNext()) {
            subjects.add(
                Subject(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(SubjectsTable.COL_ID)),
                    userId = cursor.getLong(cursor.getColumnIndexOrThrow(SubjectsTable.COL_USER_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(SubjectsTable.COL_NAME)),
                    createdAt = cursor.getString(cursor.getColumnIndexOrThrow(SubjectsTable.COL_CREATED_AT))
                )
            )
        }

        cursor.close()
        db.close()
        return subjects
    }

    fun deleteSubject(subjectId: Long): Int {
        val db = dbHelper.writableDatabase
        val rows = db.delete(
            SubjectsTable.TABLE_NAME,
            "${SubjectsTable.COL_ID} = ?",
            arrayOf(subjectId.toString())
        )
        db.close()
        return rows
    }
}