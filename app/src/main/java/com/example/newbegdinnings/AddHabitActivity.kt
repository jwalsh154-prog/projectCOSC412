package com.example.newbegdinnings

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import android.util.Log



class AddHabitActivity : AppCompatActivity() {
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_habit)

        val dbHelper = DatabaseHelper(this)
        val nameField = findViewById<EditText>(R.id.habitName)
        val categoryField = findViewById<EditText>(R.id.habitCategory)
        val freqField = findViewById<EditText>(R.id.habitFrequency)
        val startDateField = findViewById<EditText>(R.id.habitStartDate)
        val saveButton = findViewById<Button>(R.id.saveHabit)

        saveButton.setOnClickListener {
            Log.d("HABIT_CLICK", "Save button was tapped")
            val name = nameField.text.toString()
            val category = categoryField.text.toString()
            val frequency = freqField.text.toString()
            val startDate = startDateField.text.toString()

            if (name.isNotBlank()) {
                val result = dbHelper.insertHabit(name, category, frequency, startDate)

                if (result == -1L) {
                    Toast.makeText(this, "Failed to save habit!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Habit saved!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, DashboardActivity::class.java))
                    finish()
                }
            } else {
                Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
