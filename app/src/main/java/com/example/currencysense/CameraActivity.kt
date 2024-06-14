package com.example.currencysense

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class CameraActivity : AppCompatActivity() {

    private lateinit var viewFinder: PreviewView
    private var imageCapture: ImageCapture? = null
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var executor: ScheduledExecutorService
    private lateinit var tfliteModel: TFLiteModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        mediaPlayer = MediaPlayer.create(this, R.raw.scan_audio)

        // Initialize the executor
        executor = Executors.newSingleThreadScheduledExecutor()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }

        viewFinder = findViewById(R.id.viewFinder)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        tfliteModel = TFLiteModel(this)  // Initialize TFLite model

        val captureButton: Button = findViewById(R.id.captureButton)
        captureButton.setOnClickListener { takePhoto() }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)

            } catch (exc: Exception) {
                Toast.makeText(this, "Failed to bind camera use cases", Toast.LENGTH_SHORT).show()
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture?.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(baseContext, "Photo capture failed: ${exc.message}", Toast.LENGTH_SHORT).show()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = output.savedUri ?: Uri.fromFile(photoFile)
                    val bitmap = BitmapFactory.decodeFile(photoFile.path)
                    val prediction = tfliteModel.predict(bitmap)

                    // Adding logging to see prediction values
                    Log.d("CameraActivity", "Prediction: ${prediction.contentToString()}")

                    // Decipher the prediction
                    val nominal = intArrayOf(10000, 5000, 50000)
                    val maxPrediction = prediction.maxOrNull() ?: 0f
                    val predClass = prediction.withIndex().maxByOrNull { it.value }?.index ?: -1

                    // Apply confidence threshold
                    val confidenceThreshold = 1.0f
                    val recognizedAmount = if (maxPrediction > confidenceThreshold && predClass != -1) nominal[predClass] else -1

                    // Logging for debugging
                    Log.d("CameraActivity", "Max Prediction: $maxPrediction, Predicted Class: $predClass, Recognized Amount: $recognizedAmount")

                    val intent = Intent(this@CameraActivity, ResultActivity::class.java).apply {
                        putExtra("IMAGE_URI", savedUri.toString())
                        putExtra("RECOGNIZED_AMOUNT", recognizedAmount)
                    }
                    startActivity(intent)
                }
            }
        )
    }


    private val outputDirectory: File by lazy {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        mediaDir ?: filesDir
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
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
        }, 0, 5, TimeUnit.SECONDS)
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

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }
}
