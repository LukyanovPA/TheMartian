package com.pavellukyanov.themartian.data.api.interceptors

import com.pavellukyanov.themartian.BuildConfig
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import java.net.URI

// mars.jpl.nasa.gov is the legacy host of older raw images; it redirects to mars.nasa.gov, bypassing the relay
private val NASA_IMAGE_HOSTS = setOf("mars.nasa.gov", "mars.jpl.nasa.gov")

private const val RELAY_IMAGE_PREFIX = "/nasa"

class ImageRelayInterceptor : Interceptor {

    private val relayOrigin: HttpUrl = URI(BuildConfig.BASE_URL).let { relay ->
        HttpUrl.Builder()
            .scheme(relay.scheme)
            .host(relay.host)
            .apply { if (relay.port != -1) port(relay.port) }
            .build()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (request.url.host !in NASA_IMAGE_HOSTS) return chain.proceed(request)

        val relayedUrl = relayOrigin.newBuilder()
            .encodedPath(RELAY_IMAGE_PREFIX + request.url.encodedPath)
            .encodedQuery(request.url.encodedQuery)
            .build()

        return chain.proceed(request.newBuilder().url(relayedUrl).build())
    }
}
