package com.example.currencysense

import android.content.Context
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder

class TFLiteModel(context: Context) {

    private val interpreter: Interpreter

    init {
        val model = FileUtil.loadMappedFile(context, "model.tflite")
        interpreter = Interpreter(model)
    }

    fun predict(image: ByteBuffer): FloatArray {
        // Assuming the model output is a single float array
        val output = Array(1) { FloatArray(2) } // Change dimensions according to your model's output
        interpreter.run(image, output)
        return output[0]
    }
}
