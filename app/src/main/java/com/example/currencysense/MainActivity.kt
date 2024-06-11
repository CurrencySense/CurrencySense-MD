package com.example.currencysense

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        enableEdgeToEdge()

        // Set up window insets for the main layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set click listener for scan_button
        findViewById<View>(R.id.scan_button).setOnClickListener {
            // Start CameraActivity when scan_button is clicked
            startActivity(Intent(this@MainActivity, CameraActivity::class.java))
        }

        // Go to History Activity
        findViewById<Button>(R.id.history_button).setOnClickListener {
            startActivity(Intent(this@MainActivity, HistoryActivity::class.java))
        }

    }
}
