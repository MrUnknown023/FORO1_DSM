package com.sv.udb.registronotasapp.data.db

object DbStruct {
    const val DATABASE_NAME = "regitro_de_notas.db"
    const val DATABASE_VERSION = 1

    object UsersTable {
        const val TABLE_NAME = "users"
        const val COL_ID = "id"
        const val COL_EMAIL = "email"
        const val COL_PASSWORD = "password"
        const val COL_DISPLAY_NAME = "display_name"
    }

    object SubjectsTable {
        const val TABLE_NAME = "subjects"
        const val COL_ID = "id"
        const val COL_USER_ID = "user_id"
        const val COL_NAME = "name"
        const val COL_CREATED_AT = "created_at"
    }

    object ActivitiesTable {
        const val TABLE_NAME = "activities"
        const val COL_ID = "id"
        const val COL_SUBJECT_ID = "subject_id"
        const val COL_NAME = "name"
        const val COL_PERCENTAGE = "percentage"
        const val COL_SCORE = "score"
        const val COL_CREATED_AT = "created_at"
    }
}