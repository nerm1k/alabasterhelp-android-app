package com.example.alabasterhelp2

import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import java.util.Calendar

class AddReminderActivity : AppCompatActivity() {
    private lateinit var editTextTitle: EditText
    private lateinit var spinnerDuration: Spinner
    private lateinit var spinnerFrequency: Spinner
    private lateinit var radioGroupConfirmationMethod: RadioGroup
    private lateinit var timePicker: TimePicker
    private lateinit var buttonAddReminder: Button
    private lateinit var buttonTime: Button
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var radioPhoto: RadioButton
    private lateinit var radioMath: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_reminder)


        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Показываем кнопку "Назад"
        supportActionBar?.title = null
        supportActionBar?.setHomeAsUpIndicator(R.drawable.arrow_back) // Укажите имя вашего изображения

        editTextTitle = findViewById(R.id.editTextTitle)
        spinnerDuration = findViewById(R.id.spinnerDuration)
        spinnerFrequency = findViewById(R.id.spinnerFrequency)
        radioGroupConfirmationMethod = findViewById(R.id.radioGroupConfirmationMethod)
        radioPhoto = findViewById(R.id.radioPhoto)
        radioMath = findViewById(R.id.radioMath)
        // timePicker = findViewById(R.id.timePicker)
        buttonAddReminder = findViewById(R.id.buttonAddReminder)
        buttonTime = findViewById(R.id.buttonTime)

        buttonTime.setOnClickListener {
            showTimePickerDialog()
        }

        radioGroupConfirmationMethod.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radioPhoto -> {
                    // Измените изображение для radioPhoto
                    radioPhoto.setCompoundDrawablesWithIntrinsicBounds(R.drawable.photo_add_selected, 0, 0, 0)
                    radioMath.setCompoundDrawablesWithIntrinsicBounds(R.drawable.math_add, 0, 0, 0) // Сбросить изображение для radioMath
                }
                R.id.radioMath -> {
                    // Измените изображение для radioMath
                    radioMath.setCompoundDrawablesWithIntrinsicBounds(R.drawable.math_add_selected, 0, 0, 0)
                    radioPhoto.setCompoundDrawablesWithIntrinsicBounds(R.drawable.photo_add, 0, 0, 0) // Сбросить изображение для radioPhoto
                }
            }
        }

        databaseHelper = DatabaseHelper(this)

        buttonAddReminder.setOnClickListener {
            addReminder()
        }

        setupFrequencySpinner()
        setupDurationSpinner()

    }


    private fun showTimePickerDialog() {
        // Получаем текущее время
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        // Создаем TimePickerDialog
        val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            // Форматируем выбранное время
            val time = String.format("%02d:%02d", selectedHour, selectedMinute)
            // Устанавливаем текст кнопки
            buttonTime.text = time
        }, hour, minute, true) // true для 24-часового формата

        // Показываем диалог выбора времени
        timePickerDialog.show()
    }

    private fun setupDurationSpinner() {
        val durations = arrayOf("1 день", "3 дня", "7 дней", "30 дней", "90 дней")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, durations)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDuration.adapter = adapter

    }

    private fun setupFrequencySpinner() {
        val frequencies = arrayOf("Ежедневно", "Еженедельно", "Ежемесячно")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, frequencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFrequency.adapter = adapter
    }

    private fun addReminder() {
        val title = editTextTitle.text.toString()
        val duration = spinnerDuration.selectedItem.toString()
        val frequency = spinnerFrequency.selectedItem.toString()
        val confirmationMethod = if (radioGroupConfirmationMethod.checkedRadioButtonId == R.id.radioPhoto) "Фото" else "Математический пример"
        val time = "${timePicker.hour}:${timePicker.minute}"

        databaseHelper.addReminder(title, duration, frequency, confirmationMethod, time)

        Toast.makeText(this, "Напоминание добавлено", Toast.LENGTH_SHORT).show()
        finish() // Закрыть активность после добавления
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish() // Закрываем текущую активность
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "reminders.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "reminders"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_DURATION = "duration"
        private const val COLUMN_FREQUENCY = "frequency"
        private const val COLUMN_CONFIRMATION_METHOD = "confirmation_method"
        private const val COLUMN_TIME = "time"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = ("CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_TITLE TEXT," +
                "$COLUMN_DURATION TEXT," +
                "$COLUMN_FREQUENCY TEXT," +
                "$COLUMN_CONFIRMATION_METHOD TEXT," +
                "$COLUMN_TIME TEXT)")
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addReminder(title: String, duration: String, frequency: String, confirmationMethod: String, time: String) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_TITLE, title)
        contentValues.put(COLUMN_DURATION, duration)
        contentValues.put(COLUMN_FREQUENCY, frequency)
        contentValues.put(COLUMN_CONFIRMATION_METHOD, confirmationMethod)
        contentValues.put(COLUMN_TIME, time)
        db.insert(TABLE_NAME, null, contentValues)
        db.close()
    }
}