package com.example.newbegdinnings

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(private val context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        Log.d("DB_PATH", "Database path: ${context.getDatabasePath(DATABASE_NAME)}")
        val createTable = """
            CREATE TABLE $TABLE_HABITS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_CATEGORY TEXT,
                $COLUMN_FREQUENCY TEXT,
                $COLUMN_START_DATE TEXT
            );
        """.trimIndent()
        db.execSQL(createTable)

        Log.d("DB_PATH", db.path)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_HABITS")
        onCreate(db)
    }

    fun insertHabit(name: String, category: String, frequency: String, startDate: String): Long {
        val db = writableDatabase
        val vName = name
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_CATEGORY, category)
            put(COLUMN_FREQUENCY, frequency)
            put(COLUMN_START_DATE, startDate)
            Log.d("DB_INSERT", "Habit inserted successfully: $vName")
        }
        val result = db.insert(TABLE_HABITS, null, values)
        db.close()
        return result
    }

    fun getAllHabits(): List<Habit> {
        val db = readableDatabase
        val habits = mutableListOf<Habit>()

        val cursor = db.rawQuery("SELECT * FROM $TABLE_HABITS", null)
        cursor.use {
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                    val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                    val category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY))
                    val frequency = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FREQUENCY))
                    val startDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_DATE))
                    habits.add(Habit(id, name, category, frequency, startDate))
                } while (cursor.moveToNext())
            }
        }
        db.close()
        return habits
    }

    fun deleteHabit(id: Int): Int {
        val db = writableDatabase
        return try {
            db.delete(TABLE_HABITS, "$COLUMN_ID = ?", arrayOf(id.toString()))
        } finally {
            db.close()
        }
    }

    companion object {
        private const val DATABASE_NAME = "new_beginnings_db"
        private const val DATABASE_VERSION = 1

        const val TABLE_HABITS = "habits"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_CATEGORY = "category"
        const val COLUMN_FREQUENCY = "frequency"
        const val COLUMN_START_DATE = "startDate"
    }
}