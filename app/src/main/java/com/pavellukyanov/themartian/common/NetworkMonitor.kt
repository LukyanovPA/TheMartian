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

        // A transport type alone proves nothing: a hotel or cafe Wi-Fi awaiting sign-in still
        // reports TRANSPORT_WIFI, and requests over it stall on the socket until the read
        // timeout expires. CAPTIVE_PORTAL is the one case the system has positively
        // identified, so it is rejected up front and the user gets a clear message at once.
        //
        // NET_CAPABILITY_VALIDATED would catch more, but it is deliberately not used: it is
        // briefly unset right after a network connects and on some VPN setups, and a false
        // "no internet" would be worse than the stall it prevents.
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            !networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_CAPTIVE_PORTAL)
    }
}

class NetworkStateException(message: String) : Exception(message)