package com.example.currencysense

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.currencysense.utils.formatCurrency

class ResultActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        mediaPlayer = MediaPlayer()

        val imageView: ImageView = findViewById(R.id.imageView)
        val amountTextView: TextView = findViewById(R.id.amountTextView)
        val prefixTextView: TextView = findViewById(R.id.prefixTextView)

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
            val formattedAmount = formatCurrency(recognizedAmount)
            prefixTextView.text = "Jumlah Uang Anda adalah:"
            amountTextView.text = formattedAmount
            // Play corresponding audio based on recognized amount
            when (recognizedAmount) {
                50000 -> playAudioFromRaw(R.raw.lima_puluh_ribu_audio)
                10000 -> playAudioFromRaw(R.raw.sepuluh_ribu_audio)
                5000 -> playAudioFromRaw(R.raw.lima_ribu_audio)
                else -> Toast.makeText(this, "Unrecognized amount", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "No recognized amount provided", Toast.LENGTH_SHORT).show()
        }
    }


    private fun playAudioFromRaw(rawResourceId: Int) {
        try {
            mediaPlayer = MediaPlayer.create(this, rawResourceId)
            mediaPlayer.start()
        } catch (e: Exception) {
            Toast.makeText(this, "Error playing audio", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release() // Release MediaPlayer resources
    }
}
