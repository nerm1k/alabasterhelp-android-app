// AddReminderActivity.kt
package com.example.alabasterhelp2

import android.app.TimePickerDialog
import android.content.ContentValues
import android.os.Bundle
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.snackbar.Snackbar
import java.util.Calendar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddReminderActivity : AppCompatActivity() {
    private lateinit var editTextTitle: EditText
    private lateinit var spinnerDuration: Spinner
    private lateinit var spinnerFrequency: Spinner
    private lateinit var radioGroupConfirmationMethod: RadioGroup
    private lateinit var buttonAddReminder: Button
    private lateinit var buttonTime: Button
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var radioPhoto: RadioButton
    private lateinit var radioMath: RadioButton

    private var selectedHour: Int = 0
    private var selectedMinute: Int = 0
    private var selectedTimestamp: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_reminder)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = null
        supportActionBar?.setHomeAsUpIndicator(R.drawable.arrow_back)

        editTextTitle = findViewById(R.id.editTextTitle)
        spinnerDuration = findViewById(R.id.spinnerDuration)
        spinnerFrequency = findViewById(R.id.spinnerFrequency)
        radioGroupConfirmationMethod = findViewById(R.id.radioGroupConfirmationMethod)
        radioPhoto = findViewById(R.id.radioPhoto)
        radioMath = findViewById(R.id.radioMath)
        buttonAddReminder = findViewById(R.id.buttonAddReminder)
        buttonTime = findViewById(R.id.buttonTime)

        buttonTime.setOnClickListener {
            showTimePickerDialog()
        }

        radioGroupConfirmationMethod.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radioPhoto -> {
                    radioPhoto.setCompoundDrawablesWithIntrinsicBounds(R.drawable.photo_add_selected, 0, 0, 0)
                    radioMath.setCompoundDrawablesWithIntrinsicBounds(R.drawable.math_add, 0, 0, 0)
                }
                R.id.radioMath -> {
                    radioMath.setCompoundDrawablesWithIntrinsicBounds(R.drawable.math_add_selected, 0, 0, 0)
                    radioPhoto.setCompoundDrawablesWithIntrinsicBounds(R.drawable.photo_add, 0, 0, 0)
                }
            }
        }

        databaseHelper = DatabaseHelper(this)

        buttonAddReminder.setOnClickListener {
            addReminder()
        }

        setupDurationSpinner()
        setupFrequencySpinner()
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            this.selectedHour = selectedHour
            this.selectedMinute = selectedMinute
            // Устанавливаем timestamp на сегодняшний день с выбранным временем
            val selectedCal = Calendar.getInstance()
            selectedCal.set(Calendar.HOUR_OF_DAY, selectedHour)
            selectedCal.set(Calendar.MINUTE, selectedMinute)
            selectedCal.set(Calendar.SECOND, 0)
            selectedCal.set(Calendar.MILLISECOND, 0)
            selectedTimestamp = selectedCal.timeInMillis

            val time = String.format("%02d:%02d", selectedHour, selectedMinute)
            buttonTime.text = time
        }, hour, minute, true) // true для 24-часового формата

        timePickerDialog.show()
    }
    private fun setupDurationSpinner() {
        val durations = arrayOf("1 день", "3 дня", "7 дней", "30 дней", "90 дней")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, durations)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDuration.adapter = adapter
    }

    private fun setupFrequencySpinner() {
        val frequencies = arrayOf("Ежедневно", "Еженедельно", "Ежемесячно", "Не повторять")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, frequencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFrequency.adapter = adapter
    }

    private fun addReminder() {
        val title = editTextTitle.text.toString().trim()
        val duration = spinnerDuration.selectedItem.toString()
        val frequency = spinnerFrequency.selectedItem.toString()

        val confirmationMethod = when (radioGroupConfirmationMethod.checkedRadioButtonId) {
            R.id.radioPhoto -> "Фото"
            R.id.radioMath -> "Математический пример"
            else -> "Не выбрано"
        }

        // Проверка, что время выбрано
        if (buttonTime.text.toString() == "Выбрать время") {
            Snackbar.make(findViewById(android.R.id.content), "Выберите время напоминания", Snackbar.LENGTH_SHORT).show()
            return
        }

        if (title.isEmpty()) {
            Snackbar.make(findViewById(android.R.id.content), "Введите название напоминания", Snackbar.LENGTH_SHORT).show()
            return
        }

        // Создание объекта напоминания
        val reminder = Reminder(
            title = title,
            duration = duration,
            frequency = if (frequency == "Не повторять") null else frequency,
            confirmationMethod = confirmationMethod,
            timestamp = selectedTimestamp,
            lastCompleted = null,
            photoPath = null
        )

        // Вставка напоминания в базу данных
        GlobalScope.launch(Dispatchers.IO) {
            val id = databaseHelper.addReminder(reminder)
            if (id != -1L) {
                launch(Dispatchers.Main) {
                    Snackbar.make(findViewById(android.R.id.content), "Напоминание добавлено", Snackbar.LENGTH_SHORT).show()
                    finish() // Закрыть активность после добавления
                }
            } else {
                launch(Dispatchers.Main) {
                    Snackbar.make(findViewById(android.R.id.content), "Ошибка при добавлении напоминания", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
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