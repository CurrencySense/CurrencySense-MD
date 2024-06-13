package com.example.currencysense

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class ResultActivity : AppCompatActivity() {

    val prediction = intent.getIntArrayExtra("PREDICTION")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val imageView: ImageView = findViewById(R.id.imageView)
        val resultTextView: TextView = findViewById(R.id.amountTextView)

        // Print intent extras for debugging
        val imageUri = intent.getStringExtra("IMAGE_URI")
        val prediction = intent.getIntArrayExtra("PREDICTION")
        Log.d("ResultActivity", "Image URI: $imageUri")
        Log.d("ResultActivity", "Prediction: ${prediction?.contentToString()}")

        imageUri?.let {
            Glide.with(this)
                .load(Uri.parse(it))
                .into(imageView)
        }

        prediction?.let {
            resultTextView.text = "Total: ${it.sum()} (${it.joinToString(", ")})"
        }

        // Check if prediction is null or empty before accessing
        if (prediction != null && prediction.isNotEmpty()) {
            // Process and display prediction
        } else {
            // Handle case where no money was detected
            Toast.makeText(this, "No money detected in the image", Toast.LENGTH_SHORT).show()
            // Optionally, navigate back or show a message to the user
        }
    }
}
