// DatabaseHelper.kt
package com.example.alabasterhelp2

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.database.Cursor

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "reminders.db"
        private const val DATABASE_VERSION = 2 // Увеличиваем версию для миграции

        const val TABLE_REMINDERS = "reminders"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_DURATION = "duration"
        const val COLUMN_FREQUENCY = "frequency"
        const val COLUMN_CONFIRMATION_METHOD = "confirmation_method"
        const val COLUMN_TIMESTAMP = "timestamp" // Новое поле для времени
        const val COLUMN_LAST_COMPLETED = "last_completed" // Новое поле для последнего выполнения
        const val COLUMN_PHOTO_PATH = "photo_path" // Новое поле для пути к фото (опционально)
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = """
            CREATE TABLE $TABLE_REMINDERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITLE TEXT NOT NULL,
                $COLUMN_DURATION TEXT NOT NULL,
                $COLUMN_FREQUENCY TEXT,
                $COLUMN_CONFIRMATION_METHOD TEXT NOT NULL,
                $COLUMN_TIMESTAMP INTEGER NOT NULL,
                $COLUMN_LAST_COMPLETED INTEGER,
                $COLUMN_PHOTO_PATH TEXT
            )
        """.trimIndent()
        db?.execSQL(createTable)
    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            // Добавляем новое поле timestamp вместо hour и minute
            db?.execSQL("ALTER TABLE $TABLE_REMINDERS ADD COLUMN $COLUMN_TIMESTAMP INTEGER")
            db?.execSQL("ALTER TABLE $TABLE_REMINDERS ADD COLUMN $COLUMN_LAST_COMPLETED INTEGER")
            db?.execSQL("ALTER TABLE $TABLE_REMINDERS ADD COLUMN $COLUMN_PHOTO_PATH TEXT")
            // Если необходимо, удалите старые столбцы hour и minute
            // SQLite не поддерживает удаление столбцов напрямую. Нужно создать новую таблицу без них и перенести данные.
        }
        // Добавьте дополнительные миграции при необходимости
    }

    // Метод для добавления нового напоминания
    fun addReminder(reminder: Reminder): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, reminder.title)
            put(COLUMN_DURATION, reminder.duration)
            put(COLUMN_FREQUENCY, reminder.frequency)
            put(COLUMN_CONFIRMATION_METHOD, reminder.confirmationMethod)
            put(COLUMN_TIMESTAMP, reminder.timestamp)
            put(COLUMN_LAST_COMPLETED, reminder.lastCompleted)
            put(COLUMN_PHOTO_PATH, reminder.photoPath)
        }
        val id = db.insert(TABLE_REMINDERS, null, values)
        db.close()
        return id
    }

    // Метод для получения всех напоминаний
    fun getAllReminders(): List<Reminder> {
        val reminders = mutableListOf<Reminder>()
        val selectQuery = "SELECT * FROM $TABLE_REMINDERS"
        val db = this.readableDatabase
        val cursor: Cursor? = db.rawQuery(selectQuery, null)

        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    val reminder = Reminder(
                        id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID)),
                        title = it.getString(it.getColumnIndexOrThrow(COLUMN_TITLE)),
                        duration = it.getString(it.getColumnIndexOrThrow(COLUMN_DURATION)),
                        frequency = it.getString(it.getColumnIndexOrThrow(COLUMN_FREQUENCY)),
                        confirmationMethod = it.getString(it.getColumnIndexOrThrow(COLUMN_CONFIRMATION_METHOD)),
                        timestamp = it.getLong(it.getColumnIndexOrThrow(COLUMN_TIMESTAMP)),
                        lastCompleted = if (!it.isNull(it.getColumnIndexOrThrow(COLUMN_LAST_COMPLETED))) {
                            it.getLong(it.getColumnIndexOrThrow(COLUMN_LAST_COMPLETED))
                        } else {
                            null
                        },
                        photoPath = if (!it.isNull(it.getColumnIndexOrThrow(COLUMN_PHOTO_PATH))) {
                            it.getString(it.getColumnIndexOrThrow(COLUMN_PHOTO_PATH))
                        } else {
                            null
                        }
                    )
                    reminders.add(reminder)
                } while (it.moveToNext())
            }
        }
        db.close()
        return reminders
    }

    // Метод для обновления напоминания
    fun updateReminder(reminder: Reminder): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, reminder.title)
            put(COLUMN_DURATION, reminder.duration)
            put(COLUMN_FREQUENCY, reminder.frequency)
            put(COLUMN_CONFIRMATION_METHOD, reminder.confirmationMethod)
            put(COLUMN_TIMESTAMP, reminder.timestamp)
            put(COLUMN_LAST_COMPLETED, reminder.lastCompleted)
            put(COLUMN_PHOTO_PATH, reminder.photoPath)
        }
        val rowsAffected = db.update(
            TABLE_REMINDERS,
            values,
            "$COLUMN_ID = ?",
            arrayOf(reminder.id.toString())
        )
        db.close()
        return rowsAffected
    }
    // Метод для получения напоминания по ID
    fun getReminderById(id: Int): Reminder? {
        val selectQuery = "SELECT * FROM $TABLE_REMINDERS WHERE $COLUMN_ID = ?"
        val db = this.readableDatabase
        val cursor: Cursor? = db.rawQuery(selectQuery, arrayOf(id.toString()))
        var reminder: Reminder? = null

        cursor?.use {
            if (it.moveToFirst()) {
                reminder = Reminder(
                    id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID)),
                    title = it.getString(it.getColumnIndexOrThrow(COLUMN_TITLE)),
                    duration = it.getString(it.getColumnIndexOrThrow(COLUMN_DURATION)),
                    frequency = it.getString(it.getColumnIndexOrThrow(COLUMN_FREQUENCY)),
                    confirmationMethod = it.getString(it.getColumnIndexOrThrow(COLUMN_CONFIRMATION_METHOD)),
                    timestamp = it.getLong(it.getColumnIndexOrThrow(COLUMN_TIMESTAMP)),
                    lastCompleted = if (!it.isNull(it.getColumnIndexOrThrow(COLUMN_LAST_COMPLETED))) {
                        it.getLong(it.getColumnIndexOrThrow(COLUMN_LAST_COMPLETED))
                    } else {
                        null
                    },
                    photoPath = if (!it.isNull(it.getColumnIndexOrThrow(COLUMN_PHOTO_PATH))) {
                        it.getString(it.getColumnIndexOrThrow(COLUMN_PHOTO_PATH))
                    } else {
                        null
                    }
                )
            }
        }
        db.close()
        return reminder
    }
}

data class Reminder(
    val id: Int = 0, // Автоматически генерируется базой данных
    val title: String,
    val duration: String,
    val frequency: String?,
    val confirmationMethod: String,
    val timestamp: Long, // Единое поле для времени в формате timestamp
    val lastCompleted: Long? = null, // Время последнего выполнения
    val photoPath: String? = null // Путь к сохраненному фото (опционально)
)