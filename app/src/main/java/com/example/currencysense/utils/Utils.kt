package com.example.currencysense.utils

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import java.text.DecimalFormat

fun internetAccessChecked(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val activityNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

    return when {
        activityNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        activityNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

        else -> false
    }
}

fun formatCurrency(amount: Int): String {
    val formatter = DecimalFormat("#,###")
    return "Rp. ${formatter.format(amount)}"
}

fun showErrorMessage(context: Context, message: String) {
    val builder = AlertDialog.Builder(context)
    builder.setTitle("You're Online")
    builder.setMessage(message)
    builder.setIcon(android.R.drawable.ic_dialog_alert)

    builder.setPositiveButton("Close") { dialog, which ->
        dialog.dismiss()

    }

    val dialog = builder.create()
    dialog.show()
}