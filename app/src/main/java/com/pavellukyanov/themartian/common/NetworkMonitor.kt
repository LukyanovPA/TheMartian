package com.pavellukyanov.themartian.common

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.pavellukyanov.themartian.R
import com.pavellukyanov.themartian.data.api.ApiException

class NetworkMonitor(
    private val context: Context
) {
    suspend operator fun <T : Any> invoke(onAction: suspend () -> T): T =
        if (isNetworkAvailable()) onAction()
        else throw ApiException.ConnectionException(message = context.getString(R.string.bad_internet_connection_error_message))

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}