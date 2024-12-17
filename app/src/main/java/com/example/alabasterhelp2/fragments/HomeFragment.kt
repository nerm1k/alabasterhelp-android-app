package com.example.alabasterhelp2.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.alabasterhelp2.AddReminderActivity
import com.example.alabasterhelp2.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val fabAddReminder: FloatingActionButton = view.findViewById(R.id.fab_add_reminder)
        fabAddReminder.setOnClickListener {
            // Открытие экрана для добавления напоминания
            val intent = Intent(activity, AddReminderActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}