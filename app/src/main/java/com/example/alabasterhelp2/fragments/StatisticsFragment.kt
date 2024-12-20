package com.example.alabasterhelp2.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.alabasterhelp2.DatabaseHelper
import com.example.alabasterhelp2.R
import com.example.alabasterhelp2.ReminderAdapter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class StatisticsFragment : Fragment() {
    private lateinit var textViewYear: TextView
    private lateinit var textViewMonth: TextView

    private var currentCalendar = Calendar.getInstance()

    private lateinit var recyclerViewReminders: RecyclerView
    private lateinit var reminderAdapter: ReminderAdapter
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_statistics, container, false)

        textViewYear = view.findViewById(R.id.textViewYear)
        textViewMonth = view.findViewById(R.id.textViewMonth)

        updateMonthYear()
        populateDays()

        view.findViewById<Button>(R.id.buttonLeft).setOnClickListener {
            // Переключение на предыдущую неделю
            currentCalendar.add(Calendar.WEEK_OF_YEAR, -2)
            updateMonthYear()
            populateDays()
        }

        view.findViewById<Button>(R.id.buttonRight).setOnClickListener {
            // Переключение на следующую неделю
            currentCalendar.add(Calendar.WEEK_OF_YEAR, 2)
            updateMonthYear()
            populateDays()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        populateDays()

    }

    private fun isToday(timestamp: Long): Boolean {
        val current = Calendar.getInstance()
        val reminderDate = Calendar.getInstance()
        reminderDate.timeInMillis = timestamp

        return current.get(Calendar.YEAR) == reminderDate.get(Calendar.YEAR) &&
                current.get(Calendar.DAY_OF_YEAR) == reminderDate.get(Calendar.DAY_OF_YEAR)
    }

    private fun updateMonthYear() {
        val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
        val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())

        // Обновляем текст для month и year
        textViewMonth.text = monthFormat.format(currentCalendar.time)
        textViewYear.text = yearFormat.format(currentCalendar.time)
    }

    private fun populateDays() {
        // Устанавливаем на начало недели (понедельник)
        val calendar = currentCalendar.clone() as Calendar
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        // Сброс значений всех TextView
        resetTextViews()

        // Получаем текущую дату
        val today = Calendar.getInstance()

        // Заполняем TextView с датами текущей недели
        for (i in 0 until 14) {
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH) + 1 // Месяцы начинаются с 0
            val year = calendar.get(Calendar.YEAR)

            // Определяем, какой TextView обновить в зависимости от дня недели
            when (i) {
                0 -> view?.findViewById<TextView>(R.id.mondayDate)?.text = "$day"
                1 -> view?.findViewById<TextView>(R.id.tuesdayDate)?.text = "$day"
                2 -> view?.findViewById<TextView>(R.id.wednesdayDate)?.text = "$day"
                3 -> view?.findViewById<TextView>(R.id.thursdayDate)?.text = "$day"
                4 -> view?.findViewById<TextView>(R.id.fridayDate)?.text = "$day"
                5 -> view?.findViewById<TextView>(R.id.saturdayDate)?.text = "$day"
                6 -> view?.findViewById<TextView>(R.id.sundayDate)?.text = "$day"
                7 -> view?.findViewById<TextView>(R.id.mondayDate_2)?.text = "$day"
                8 -> view?.findViewById<TextView>(R.id.tuesdayDate_2)?.text = "$day"
                9 -> view?.findViewById<TextView>(R.id.wednesdayDate_2)?.text = "$day"
                10 -> view?.findViewById<TextView>(R.id.thursdayDate_2)?.text = "$day"
                11 -> view?.findViewById<TextView>(R.id.fridayDate_2)?.text = "$day"
                12 -> view?.findViewById<TextView>(R.id.saturdayDate_2)?.text = "$day"
                13 -> view?.findViewById<TextView>(R.id.sundayDate_2)?.text = "$day"
            }

            // Проверяем, является ли текущая дата
            if (day == today.get(Calendar.DAY_OF_MONTH) &&
                month == today.get(Calendar.MONTH) + 1 &&
                year == today.get(Calendar.YEAR)) {
                // Обводим текущую дату в круг
                when (i) {
                    0 -> view?.findViewById<TextView>(R.id.mondayDate)?.setBackgroundResource(R.drawable.circle_background)
                    1 -> view?.findViewById<TextView>(R.id.tuesdayDate)?.setBackgroundResource(R.drawable.circle_background)
                    2 -> view?.findViewById<TextView>(R.id.wednesdayDate)?.setBackgroundResource(R.drawable.circle_background)
                    3 -> view?.findViewById<TextView>(R.id.thursdayDate)?.setBackgroundResource(R.drawable.circle_background)
                    4 -> view?.findViewById<TextView>(R.id.fridayDate)?.setBackgroundResource(R.drawable.circle_background)
                    5 -> view?.findViewById<TextView>(R.id.saturdayDate)?.setBackgroundResource(R.drawable.circle_background)
                    6 -> view?.findViewById<TextView>(R.id.sundayDate)?.setBackgroundResource(R.drawable.circle_background)
                    7 -> view?.findViewById<TextView>(R.id.mondayDate_2)?.setBackgroundResource(R.drawable.circle_background)
                    8 -> view?.findViewById<TextView>(R.id.tuesdayDate_2)?.setBackgroundResource(R.drawable.circle_background)
                    9 -> view?.findViewById<TextView>(R.id.wednesdayDate_2)?.setBackgroundResource(R.drawable.circle_background)
                    10 -> view?.findViewById<TextView>(R.id.thursdayDate_2)?.setBackgroundResource(R.drawable.circle_background)
                    11 -> view?.findViewById<TextView>(R.id.fridayDate_2)?.setBackgroundResource(R.drawable.circle_background)
                    12 -> view?.findViewById<TextView>(R.id.saturdayDate_2)?.setBackgroundResource(R.drawable.circle_background)
                    13 -> view?.findViewById<TextView>(R.id.sundayDate_2)?.setBackgroundResource(R.drawable.circle_background)
                }
            }

            // Переходим к следующему дню
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    private fun resetTextViews() {
        // Сбрасываем значения всех TextView и фон
        view?.findViewById<TextView>(R.id.mondayDate)?.apply {
            text = ""
            setBackgroundResource(0) // Сбрасываем фон
        }
        view?.findViewById<TextView>(R.id.tuesdayDate)?.apply {
            text = ""
            setBackgroundResource(0) // Сбрасываем фон
        }
        view?.findViewById<TextView>(R.id.wednesdayDate)?.apply {
            text = ""
            setBackgroundResource(0) // Сбрасываем фон
        }
        view?.findViewById<TextView>(R.id.thursdayDate)?.apply {
            text = ""
            setBackgroundResource(0) // Сбрасываем фон
        }
        view?.findViewById<TextView>(R.id.fridayDate)?.apply {
            text = ""
            setBackgroundResource(0) // Сбрасываем фон
        }
        view?.findViewById<TextView>(R.id.saturdayDate)?.apply {
            text = ""
            setBackgroundResource(0) // Сбрасываем фон
        }
        view?.findViewById<TextView>(R.id.sundayDate)?.apply {
            text = ""
            setBackgroundResource(0) // Сбрасываем фон
        }
        view?.findViewById<TextView>(R.id.mondayDate_2)?.apply {
            text = ""
            setBackgroundResource(0) // Сбрасываем фон
        }
        view?.findViewById<TextView>(R.id.tuesdayDate_2)?.apply {
            text = ""
            setBackgroundResource(0) // Сбрасываем фон
        }
        view?.findViewById<TextView>(R.id.wednesdayDate_2)?.apply {
            text = ""
            setBackgroundResource(0) // Сбрасываем фон
        }
        view?.findViewById<TextView>(R.id.thursdayDate_2)?.apply {
            text = ""
            setBackgroundResource(0) // Сбрасываем фон
        }
        view?.findViewById<TextView>(R.id.fridayDate_2)?.apply {
            text = ""
            setBackgroundResource(0) // Сбрасываем фон
        }
        view?.findViewById<TextView>(R.id.saturdayDate_2)?.apply {
            text = ""
            setBackgroundResource(0) // Сбрасываем фон
        }
        view?.findViewById<TextView>(R.id.sundayDate_2)?.apply {
            text = ""
            setBackgroundResource(0) // Сбрасываем фон
        }
    }
}

