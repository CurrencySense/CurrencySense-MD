package com.example.currencysense

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private val SPLASH_TIME_OUT: Long = 3000 // 3 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            // Start your main activity or any other activity here
            startActivity(Intent(this, MainActivity::class.java))
            finish() // close this activity
        }, SPLASH_TIME_OUT)
    }
}
