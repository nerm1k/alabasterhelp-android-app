// ViewReminderActivity.kt
package com.example.alabasterhelp2

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ViewReminderActivity : AppCompatActivity() {

    private lateinit var textViewTitle: TextView
    private lateinit var textViewCompletedAt: TextView
    private lateinit var imageViewPhoto: ImageView
    private lateinit var databaseHelper: DatabaseHelper
    private var reminderId: Int = -1
    private lateinit var currentReminder: Reminder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_reminder)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = null
        supportActionBar?.setHomeAsUpIndicator(R.drawable.arrow_back)

        textViewTitle = findViewById(R.id.textViewTitle)
        textViewCompletedAt = findViewById(R.id.textViewCompletedAt)
        imageViewPhoto = findViewById(R.id.imageViewPhoto)

        // Получение ID напоминания из Intent
        reminderId = intent.getIntExtra("REMINDER_ID", -1)
        if (reminderId == -1) {
            Toast.makeText(this, "Ошибка: напоминание не найдено", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Инициализация DatabaseHelper
        databaseHelper = DatabaseHelper(this)

        // Загрузка напоминания
        loadReminder()
    }

    private fun loadReminder() {
        GlobalScope.launch(Dispatchers.IO) {
            val reminder = databaseHelper.getReminderById(reminderId)
            if (reminder != null) {
                currentReminder = reminder
                // Обновляем UI на главном потоке
                launch(Dispatchers.Main) {
                    setupView()
                }
            } else {
                // Напоминание не найдено
                launch(Dispatchers.Main) {
                    Toast.makeText(this@ViewReminderActivity, "Напоминание не найдено", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun setupView() {
        textViewTitle.text = currentReminder.title


        if (currentReminder.confirmationMethod == "Фото" && currentReminder.photoPath != null) {
            textViewCompletedAt.text = "Вы добавили фото (${formatTimestamp(currentReminder.lastCompleted ?: 0)})"
            // Загрузка изображения из пути
            val bitmap = BitmapFactory.decodeFile(currentReminder.photoPath)
            if (bitmap != null) {
                imageViewPhoto.setImageBitmap(bitmap)
            } else {
                imageViewPhoto.setImageResource(R.drawable.photo_add)
            }
        } else {
            // Для математического примера скрываем ImageView
            imageViewPhoto.visibility = ImageView.GONE
            textViewCompletedAt.text = "Вы решили математический пример (${formatTimestamp(currentReminder.lastCompleted ?: 0)})"
        }
    }
    private fun formatTimestamp(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        return dateFormat.format(calendar.time)
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