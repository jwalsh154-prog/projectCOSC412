package com.example.newbegdinnings

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var listView: ListView
    private lateinit var habits: MutableList<Habit>
    private lateinit var adapter: ArrayAdapter<String>

    private val NOTIFICATION_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        dbHelper = DatabaseHelper(this)
        listView = findViewById(R.id.habitListView)
        val addHabitButton = findViewById<Button>(R.id.btnAddHabit)
        val remindButton = findViewById<Button>(R.id.btnRemind)

        loadHabits()

        addHabitButton.setOnClickListener {
            startActivity(Intent(this, AddHabitActivity::class.java))
            finish()
            Toast.makeText(this, "Add Habit button clicked", Toast.LENGTH_SHORT).show()
        }

        // Ask for notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!granted) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_REQUEST_CODE
                )
            }
        }

        // Start the service when "Remind" is tapped
        remindButton.setOnClickListener {
            val serviceIntent = Intent(this, ReminderService::class.java)
            startService(serviceIntent)
            Toast.makeText(this, "Reminder service started", Toast.LENGTH_SHORT).show()
        }

        // Long-press to delete habit with confirmation popup
        listView.setOnItemLongClickListener { _, _, position, _ ->
            val habitToDelete = habits[position]

            android.app.AlertDialog.Builder(this)
                .setTitle("Delete Habit")
                .setMessage("Are you sure you want to delete \"${habitToDelete.name}\" ?")
                .setPositiveButton("Yes") { _, _ ->
                    dbHelper.deleteHabit(habitToDelete.id)
                    Toast.makeText(this, "Habit deleted", Toast.LENGTH_SHORT).show()
                    loadHabits()
                }
                .setNegativeButton("Cancel", null)
                .show()

            true
        }
    }

    override fun onResume() {
        super.onResume()
        loadHabits()
    }

    private fun goToWelcomeScreen() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
    private fun loadHabits() {
        habits = dbHelper.getAllHabits().toMutableList()
        val names = habits.map { it.name }

        adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            names
        )
        listView.adapter = adapter
    }
}
