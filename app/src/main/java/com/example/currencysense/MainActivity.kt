package com.example.currencysense

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.currencysense.CameraActivity
import com.example.currencysense.R

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
    }

    private fun enableEdgeToEdge() {
        // Your implementation of edge-to-edge behavior, if needed
    }
}
