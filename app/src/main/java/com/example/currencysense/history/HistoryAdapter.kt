package com.example.currencysense.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.currencysense.R
import com.example.currencysense.data.local.HistoryData
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter(private val imagePaths: List<HistoryData>) : RecyclerView.Adapter<HistoryAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.img_item)
        val tvTitle: TextView = itemView.findViewById(R.id.tv_rupiah)
        val tvDate: TextView = itemView.findViewById(R.id.tv_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imagePath = imagePaths[position]

        Glide.with(holder.imageView.context)
            .load(imagePath.path)
            .into(holder.imageView)

        holder.tvTitle.text = imagePath.title ?: "Unknown"

        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val formattedDate = imagePath.dateTaken?.let { dateFormat.format(it) }
        holder.tvDate.text = formattedDate
    }

    override fun getItemCount(): Int {
        return imagePaths.size
    }
}
