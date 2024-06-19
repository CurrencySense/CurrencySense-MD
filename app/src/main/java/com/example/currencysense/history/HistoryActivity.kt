package com.example.currencysense.history

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.currencysense.R
import com.example.currencysense.data.local.HistoryData
import java.io.File
import java.util.Date

class HistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var historyAdapter: HistoryAdapter

    private val imagesList = mutableListOf<HistoryData>()

    companion object {
        private const val REQUEST_PERMISSION = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        recyclerView = findViewById(R.id.rv_history)
        recyclerView.layoutManager = LinearLayoutManager(this)
        historyAdapter = HistoryAdapter(imagesList)
        recyclerView.adapter = historyAdapter
    }

    override fun onResume() {
        super.onResume()
        if (checkPermission()) {
            loadImages()
        } else {
            requestPermission()
        }
    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            REQUEST_PERMISSION
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun  loadImages() {
        val externalStorage = Environment.getExternalStorageDirectory().absolutePath
        val directory = File("$externalStorage/Android/media/com.example.currencysense/CurrencySense")

        if (!directory.exists() || !directory.isDirectory) {
            Toast.makeText(this, "Directory not found!", Toast.LENGTH_SHORT).show()
            return
        }

        val files = directory.listFiles { file ->
            file.isFile && (file.extension.equals("jpg", ignoreCase = true) || file.extension.equals("jpeg", ignoreCase = true) || file.extension.equals("png", ignoreCase = true))
        }

        files?.forEach { file ->
            imagesList.add(HistoryData(file.absolutePath, file.name, Date(file.lastModified())))
        }

        historyAdapter.notifyDataSetChanged()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadImages()
                } else {
                    Toast.makeText(this, "Permission denied to read external storage", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }
}