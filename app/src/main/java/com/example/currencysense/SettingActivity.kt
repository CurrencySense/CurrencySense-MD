package com.example.currencysense

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.currencysense.utils.CustomButton

class SettingActivity : AppCompatActivity() {

    private lateinit var btnHistory: CustomButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        btnHistory = findViewById(R.id.btnHistory)

        supportActionBar?.title = "Setting"

        btnHistory.setButtonText("History")
    }
}