// ExecuteReminderActivity.kt
package com.example.alabasterhelp2

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.view.MenuItem
import android.app.AlertDialog
import android.os.Handler
import android.os.Looper
import android.text.InputType

import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView

import com.google.android.material.snackbar.Snackbar

class ExecuteReminderActivity : AppCompatActivity() {

    private lateinit var textViewTask: TextView
    private lateinit var buttonAction: Button
    private lateinit var imageView: ImageView
    private lateinit var databaseHelper: DatabaseHelper
    private var reminderId: Int = -1
    private var currentReminder: Reminder? = null

    private lateinit var takePictureLauncher: ActivityResultLauncher<Intent>

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
        const val REQUEST_PERMISSION_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_execute_reminder)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = null
        supportActionBar?.setHomeAsUpIndicator(R.drawable.arrow_back)


        imageView = findViewById(R.id.imageView)
        textViewTask = findViewById(R.id.textViewTask)
        buttonAction = findViewById(R.id.buttonAction)
        buttonAction.isEnabled = false // Отключаем кнопку до загрузки напоминания


        // Получение ID напоминания из Intent
        reminderId = intent.getIntExtra("REMINDER_ID", -1)
        if (reminderId == -1) {
            Toast.makeText(this, "Ошибка: напоминание не найдено", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Инициализация DatabaseHelper
        databaseHelper = DatabaseHelper(this)

        // Инициализация лаунчера для камеры
        takePictureLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as? Bitmap
                if (imageBitmap != null) {
                    val photoPath = saveImageToInternalStorage(imageBitmap)
                    if (photoPath != null) {
                        Toast.makeText(this, "Фото добавлено!", Toast.LENGTH_SHORT).show()
                        markAsCompleted(photoPath)
                        finish()
                    } else {
                        Toast.makeText(this, "Ошибка при сохранении фото", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Ошибка при получении фото", Toast.LENGTH_SHORT).show()
                }
            }
        }

        loadReminder()


        buttonAction.setOnClickListener {
            Log.d("ExecuteReminderActivity", "Кнопка нажата")
            currentReminder?.let { reminder ->
                val method = reminder.confirmationMethod.trim()
                Log.d("ExecuteReminderActivity", "Метод подтверждения: $method")
                when (method) {
                    "Фото" -> {
                        if (checkPermissions()) {
                            dispatchTakePictureIntent()
                        } else {
                            requestPermissions()
                        }
                    }
                    "Математический пример" -> showMathProblem()
                    else -> {
                        Toast.makeText(this, "Неизвестный метод подтверждения: $method", Toast.LENGTH_SHORT).show()
                        Log.e("ExecuteReminderActivity", "Неизвестный метод подтверждения: $method")
                    }
                }
            } ?: run {
                Log.e("ExecuteReminderActivity", "currentReminder не инициализировано")
                Toast.makeText(this, "Напоминание не загружено", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadReminder() {
        lifecycleScope.launch(Dispatchers.IO) {
            val reminder = databaseHelper.getReminderById(reminderId)
            if (reminder != null) {
                Log.d("ExecuteReminderActivity", "Напоминание загружено: $reminder")
                currentReminder = reminder
                withContext(Dispatchers.Main) {
                    setupTask()
                    buttonAction.isEnabled = true // Включаем кнопку после загрузки
                }
                val method = currentReminder?.confirmationMethod?.trim()

                if (method == "Фото") {
                    imageView.setImageResource(R.drawable.photo_add_selected)
                } else {
                    imageView.setImageResource(R.drawable.math_add_selected)
                }
            } else {
                Log.e("ExecuteReminderActivity", "Напоминание не найдено для ID: $reminderId")
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ExecuteReminderActivity, "Напоминание не найдено", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun setupTask() {
        textViewTask.text = "${currentReminder?.title}"
    }

    private fun showMathProblem() {
        val num1 = (1..10).random()
        val num2 = (1..10).random()
        val correctAnswer = num1 + num2

        Log.d("ExecuteReminderActivity", "Показ математического примера: $num1 + $num2")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Решите пример")

        // Создаём вертикальный LinearLayout для размещения TextView и EditText
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10) // Добавляем отступы для лучшего отображения
        }

        // Создаём TextView с примером
        val textView = TextView(this).apply {
            text = "$num1 + $num2 = ?"
            textSize = 18f
        }
        layout.addView(textView)

        // Создаём EditText для ввода ответа
        val input = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_NUMBER
        }
        layout.addView(input)

        // Устанавливаем созданный layout в диалоговое окно
        builder.setView(layout)

        // Обработка нажатия кнопки "Ответить"
        builder.setPositiveButton("Ответить") { dialog, _ ->
            val userInput = input.text.toString()
            val userAnswer = userInput.toIntOrNull()
            Log.d("ExecuteReminderActivity", "Пользователь ввёл: $userInput")

            // Получаем корневой view для отображения Snackbar
            val rootView: View = findViewById(android.R.id.content)

            if (userAnswer == null) {
                Snackbar.make(rootView, "Пожалуйста, введите число.", Snackbar.LENGTH_SHORT).show()
                Log.d("ExecuteReminderActivity", "Введено некорректное значение.")
            } else if (userAnswer == correctAnswer) {
                Snackbar.make(rootView, "Правильно!", Snackbar.LENGTH_SHORT).show()
                Log.d("ExecuteReminderActivity", "Пользователь ответил правильно.")
                markAsCompleted(null) // Нет фото

                // Используем Handler для задержки вызова finish(), чтобы Snackbar успел показаться
                Handler(Looper.getMainLooper()).postDelayed({
                    dialog.dismiss()
                    finish()
                }, 1500) // Задержка 2.5 секунды
            } else {
                Snackbar.make(rootView, "Неправильно. Попробуйте еще раз.", Snackbar.LENGTH_SHORT).show()
                Log.d("ExecuteReminderActivity", "Неправильный ответ: $userAnswer, правильный: $correctAnswer")
            }
        }

        // Обработка нажатия кнопки "Отмена"
        builder.setNegativeButton("Отмена") { dialog, _ ->
            dialog.cancel()
        }

        // Отображаем диалоговое окно
        builder.show()
    }

    private fun dispatchTakePictureIntent() {
        if (checkPermissions()) {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                takePictureLauncher.launch(takePictureIntent)
            } else {
                Toast.makeText(this, "Нет приложения для камеры", Toast.LENGTH_SHORT).show()
            }
        } else {
            requestPermissions()
        }
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // Разрешения предоставлены
                currentReminder?.let { reminder ->
                    if (reminder.confirmationMethod == "Фото") {
                        dispatchTakePictureIntent()
                    }
                }
            } else {
                Toast.makeText(this, "Необходимо предоставить все разрешения", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): String? {
        return try {
            val filename = "reminder_${System.currentTimeMillis()}.png"
            openFileOutput(filename, MODE_PRIVATE).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            }
            filesDir.absolutePath + "/" + filename
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun markAsCompleted(photoPath: String?) {
        val currentTime = System.currentTimeMillis()
        val updatedReminder = currentReminder?.copy(lastCompleted = currentTime, photoPath = photoPath)
        if (updatedReminder != null) {
            lifecycleScope.launch(Dispatchers.IO) {
                databaseHelper.updateReminder(updatedReminder)
            }
        }
    }

    private fun checkPermissions(): Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        return cameraPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            REQUEST_PERMISSION_CODE
        )
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