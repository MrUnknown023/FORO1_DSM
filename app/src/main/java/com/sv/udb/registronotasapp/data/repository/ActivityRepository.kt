package com.sv.udb.registronotasapp.data.repository

import android.content.ContentValues
import android.content.Context
import com.sv.udb.registronotasapp.data.db.DatabaseHelper
import com.sv.udb.registronotasapp.data.db.DbStruct.ActivitiesTable;
import com.sv.udb.registronotasapp.data.model.ActivityItem

class ActivityRepository(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun insertActivity(activity: ActivityItem): Long {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(ActivitiesTable.COL_SUBJECT_ID, activity.subjectId)
            put(ActivitiesTable.COL_NAME, activity.name)
            put(ActivitiesTable.COL_PERCENTAGE, activity.percentage)
            put(ActivitiesTable.COL_SCORE, activity.score)
            put(ActivitiesTable.COL_CREATED_AT, activity.createdAt)
        }

        val id = db.insert(ActivitiesTable.TABLE_NAME, null, values)
        db.close()
        return id
    }

    fun getActivitiesBySubject(subjectId: Long): List<ActivityItem> {
        val db = dbHelper.readableDatabase

        val cursor = db.query(
            ActivitiesTable.TABLE_NAME,
            null,
            "${ActivitiesTable.COL_SUBJECT_ID} = ?",
            arrayOf(subjectId.toString()),
            null,
            null,
            "${ActivitiesTable.COL_ID} ASC"
        )

        val activities = mutableListOf<ActivityItem>()

        while (cursor.moveToNext()) {
            activities.add(
                ActivityItem(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(ActivitiesTable.COL_ID)),
                    subjectId = cursor.getLong(cursor.getColumnIndexOrThrow(ActivitiesTable.COL_SUBJECT_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(ActivitiesTable.COL_NAME)),
                    percentage = cursor.getDouble(cursor.getColumnIndexOrThrow(ActivitiesTable.COL_PERCENTAGE)),
                    score = cursor.getDouble(cursor.getColumnIndexOrThrow(ActivitiesTable.COL_SCORE)),
                    createdAt = cursor.getString(cursor.getColumnIndexOrThrow(ActivitiesTable.COL_CREATED_AT))
                )
            )
        }

        cursor.close()
        db.close()
        return activities
    }

    fun updateActivity(activity: ActivityItem): Int {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(ActivitiesTable.COL_NAME, activity.name)
            put(ActivitiesTable.COL_PERCENTAGE, activity.percentage)
            put(ActivitiesTable.COL_SCORE, activity.score)
        }

        val rows = db.update(
            ActivitiesTable.TABLE_NAME,
            values,
            "${ActivitiesTable.COL_ID} = ?",
            arrayOf(activity.id.toString())
        )

        db.close()
        return rows
    }

    fun deleteActivity(activityId: Long): Int {
        val db = dbHelper.writableDatabase
        val rows = db.delete(
            ActivitiesTable.TABLE_NAME,
            "${ActivitiesTable.COL_ID} = ?",
            arrayOf(activityId.toString())
        )
        db.close()
        return rows
    }
}