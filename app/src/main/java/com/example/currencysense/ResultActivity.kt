package com.example.currencysense

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val imageView: ImageView = findViewById(R.id.imageView)
        val amountTextView: TextView = findViewById(R.id.amountTextView)

        val imageUri = intent.getStringExtra("IMAGE_URI")
        val recognizedAmount = intent.getIntExtra("RECOGNIZED_AMOUNT", -1)

        if (imageUri != null) {
            Glide.with(this)
                .load(Uri.parse(imageUri))
                .into(imageView)
        } else {
            Toast.makeText(this, "No image URI provided", Toast.LENGTH_SHORT).show()
            finish() // Close the activity if no image URI is provided
        }

        if (recognizedAmount != -1) {
            amountTextView.text = "Recognized Amount: $recognizedAmount"
        } else {
            Toast.makeText(this, "No recognized amount provided", Toast.LENGTH_SHORT).show()
        }
    }
}
