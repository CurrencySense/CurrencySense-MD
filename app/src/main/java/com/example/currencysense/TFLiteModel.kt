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

    fun predict(bitmap: Bitmap): Float {
        val inputSize = 224  // Change this to match your model's input size
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)

        val byteBuffer = convertBitmapToByteBuffer(resizedBitmap)
        Log.d(TAG, "Bitmap converted to ByteBuffer for TensorFlow Lite input.")

        val output = Array(1) { FloatArray(7) } // Change 7 to match your model's output size
        try {
            interpreter.run(byteBuffer, output)
            Log.d(TAG, "Inference successful.")
        } catch (e: Exception) {
            Log.e(TAG, "Error running inference: ${e.message}")
            throw RuntimeException("Error running TensorFlow Lite inference: $e")
        }

        return output[0][0]
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val inputSize = 224  // Change this to match your model's input size
        val byteBuffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * 3)
        byteBuffer.order(ByteOrder.nativeOrder())

        val intValues = IntArray(inputSize * inputSize)
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        var pixel = 0
        for (i in 0 until inputSize) {
            for (j in 0 until inputSize) {
                val value = intValues[pixel++]
                byteBuffer.putFloat(((value shr 16) and 0xFF) / 255.0f)
                byteBuffer.putFloat(((value shr 8) and 0xFF) / 255.0f)
                byteBuffer.putFloat((value and 0xFF) / 255.0f)
            }
        }

        byteBuffer.rewind()
        return byteBuffer
    }
}

