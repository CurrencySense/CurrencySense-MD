package com.example.currencysense

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var executor: ScheduledExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        enableEdgeToEdge()

        // Initialize MediaPlayer
        mediaPlayer = MediaPlayer.create(this, R.raw.main_audio)

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

    override fun onStart() {
        super.onStart()
        // Restart the audio playback when the activity is visible again
        startAudioPlayback()
    }

    private fun startAudioPlayback() {
        if (!this::executor.isInitialized || executor.isShutdown) {
            executor = Executors.newSingleThreadScheduledExecutor()
        }
        executor.scheduleAtFixedRate({
            if (!mediaPlayer.isPlaying) {
                mediaPlayer.seekTo(0)
                mediaPlayer.start()
            }
        }, 5, 15, TimeUnit.SECONDS)
    }

    override fun onStop() {
        super.onStop()
        // Stop the MediaPlayer and executor when the activity stops
        if (this::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        if (this::executor.isInitialized && !executor.isShutdown) {
            executor.shutdownNow()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release MediaPlayer resources
        if (this::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
        // Shutdown the executor service
        if (this::executor.isInitialized) {
            executor.shutdownNow()
        }
    }
}
