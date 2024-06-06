package com.example.currencysense

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val imageView: ImageView = findViewById(R.id.imageView)
        val imageUri = intent.getStringExtra("IMAGE_URI")

        imageUri?.let {
            Glide.with(this)
                .load(Uri.parse(it))
                .into(imageView)
        }
    }
}
