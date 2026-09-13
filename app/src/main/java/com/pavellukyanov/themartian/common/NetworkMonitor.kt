package com.pavellukyanov.themartian.common

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.pavellukyanov.themartian.R

class NetworkMonitor(
    private val context: Context
) {
    suspend operator fun <T : Any> invoke(onAction: suspend () -> T): T =
        if (isNetworkAvailable()) onAction()
        else throw NetworkStateException(message = context.getString(R.string.bad_internet_connection_error_message))

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            !networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_CAPTIVE_PORTAL)
    }
}

class NetworkStateException(message: String) : Exception(message)
