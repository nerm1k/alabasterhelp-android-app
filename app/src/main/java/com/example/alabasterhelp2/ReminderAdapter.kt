// ReminderAdapter.kt
package com.example.alabasterhelp2

import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar

class ReminderAdapter(
    private var reminders: List<Reminder>,
    private val onItemClick: (Reminder) -> Unit
) : RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    inner class ReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewTitle: TextView = itemView.findViewById(R.id.textViewReminderTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reminder, parent, false)
        return ReminderViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = reminders[position]
        holder.textViewTitle.text = reminder.title

        // Определяем, выполнено ли напоминание сегодня
        val isCompletedToday = reminder.lastCompleted != null && isToday(reminder.lastCompleted)

        if (isCompletedToday) {
            // Серый цвет и перечеркнутый текст
            holder.textViewTitle.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.achievement_text)
            )
            holder.textViewTitle.paintFlags = holder.textViewTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.textViewTitle.paintFlags = holder.textViewTitle.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
            // Установка иконки справа от текста
            val drawableRight = ContextCompat.getDrawable(holder.itemView.context, R.drawable.additional_info) // Замените на ваш ресурс иконки

            // Убедитесь, что drawable не равен null
            if (drawableRight != null) {
                drawableRight.setBounds(0, 0, drawableRight.intrinsicWidth, drawableRight.intrinsicHeight)

                // Создание нового drawable с вертикальным смещением
                val offsetDrawable = drawableRight.mutate()
                offsetDrawable.setBounds(0, 2, drawableRight.intrinsicWidth, drawableRight.intrinsicHeight + 6)

                holder.textViewTitle.setCompoundDrawables(null, null, offsetDrawable, null) // Установка иконки справа
                holder.textViewTitle.compoundDrawablePadding = 12
            } else {
                // Логирование или обработка случая, когда drawable не найден
                Log.e("TAG", "Drawable not found")
            }
        } else {
            // Зеленый цвет и подчеркивание
            holder.textViewTitle.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.main_green)
            )
            holder.textViewTitle.paintFlags = holder.textViewTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.textViewTitle.paintFlags = holder.textViewTitle.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        }

        holder.itemView.setOnClickListener {
            onItemClick(reminder)
        }
    }

    override fun getItemCount(): Int = reminders.size

    fun updateReminders(newReminders: List<Reminder>) {
        reminders = newReminders
        notifyDataSetChanged()
    }
    private fun isToday(timestamp: Long): Boolean {
        val current = Calendar.getInstance()
        val reminderDate = Calendar.getInstance()
        reminderDate.timeInMillis = timestamp

        return current.get(Calendar.YEAR) == reminderDate.get(Calendar.YEAR) &&
                current.get(Calendar.DAY_OF_YEAR) == reminderDate.get(Calendar.DAY_OF_YEAR)
    }
}