package com.example.alabasterhelp2

import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class AchievementDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achievement_detail)


        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Показываем кнопку "Назад"
        supportActionBar?.title = null
        supportActionBar?.setHomeAsUpIndicator(R.drawable.arrow_back) // Укажите имя вашего изображения


        // Получаем данные из Intent
        val achievementName = intent.getStringExtra("achievement_name")
        val achievementDescription = intent.getStringExtra("achievement_description")
        val achievementIsAchieved = intent.getBooleanExtra("achievement_is_achieved", false)

        // Устанавливаем данные в TextView
        findViewById<TextView>(R.id.achievement_name).text = achievementName
        findViewById<TextView>(R.id.achievement_description).text = achievementDescription
        val medalImageView: ImageView = findViewById<ImageView>(R.id.achievement_is_achieved)


        medalImageView.setImageResource(if (achievementIsAchieved) R.mipmap.ic_medalcomplete_foreground else R.mipmap.ic_medalnotcomplete_foreground)



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