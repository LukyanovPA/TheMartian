package com.pavellukyanov.themartian.data.api.interceptors

import com.pavellukyanov.themartian.BuildConfig
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import java.net.URI

private const val NASA_IMAGE_HOST = "mars.nasa.gov"

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
        if (request.url.host != NASA_IMAGE_HOST) return chain.proceed(request)

        val relayedUrl = relayOrigin.newBuilder()
            .encodedPath(RELAY_IMAGE_PREFIX + request.url.encodedPath)
            .encodedQuery(request.url.encodedQuery)
            .build()

        return chain.proceed(request.newBuilder().url(relayedUrl).build())
    }
}
