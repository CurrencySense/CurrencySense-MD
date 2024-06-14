package com.example.currencysense

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder

class TFLiteModel(context: Context) {

    private val TAG = "TFLiteModel"

    private val interpreter: Interpreter

    // Update the input size to match the model's expected input dimensions
    private val inputSize = 180
    private val modelInputSize = inputSize * inputSize * 3 // 3 channels for RGB

    private val modelOutputSize = 3 // Adjust this based on your model's output size

    init {
        try {
            val modelFile = FileUtil.loadMappedFile(context, "currency_sense_model.tflite")
            interpreter = Interpreter(modelFile)
            Log.d(TAG, "TensorFlow Lite model loaded successfully.")
        } catch (e: Exception) {
            Log.e(TAG, "Error loading TensorFlow Lite model: ${e.message}")
            throw RuntimeException("Error initializing TensorFlow Lite model: $e")
        }
    }

    fun predict(bitmap: Bitmap): Int {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)

        val byteBuffer = convertBitmapToByteBuffer(resizedBitmap)
        Log.d(TAG, "Bitmap converted to ByteBuffer for TensorFlow Lite input.")

        val output = Array(1) { FloatArray(modelOutputSize) }

        try {
            interpreter.run(byteBuffer, output)
            Log.d(TAG, "Inference successful.")
        } catch (e: Exception) {
            Log.e(TAG, "Error running inference: ${e.message}")
            throw RuntimeException("Error running TensorFlow Lite inference: $e")
        }

        // Find the index of the maximum value in the output array
        return output[0].indices.maxByOrNull { output[0][it] } ?: -1
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        // Allocate the correct size of byte buffer
        val byteBuffer = ByteBuffer.allocateDirect(modelInputSize * 4) // Assuming RGB (3 channels)
        byteBuffer.order(ByteOrder.nativeOrder())

        // Get pixel values from the resized bitmap
        val pixels = IntArray(inputSize * inputSize)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        // Normalize pixel values and populate the byteBuffer
        var pixel = 0
        for (i in 0 until inputSize) {
            for (j in 0 until inputSize) {
                val pixelValue = pixels[pixel++]

                // Normalize pixel value to [0, 1] and put into byteBuffer
                byteBuffer.putFloat(((pixelValue shr 16) and 0xFF) / 255.0f) // Red channel
                byteBuffer.putFloat(((pixelValue shr 8) and 0xFF) / 255.0f)  // Green channel
                byteBuffer.putFloat((pixelValue and 0xFF) / 255.0f)         // Blue channel
            }
        }

        byteBuffer.rewind() // Rewind the byteBuffer before use
        return byteBuffer
    }
}

