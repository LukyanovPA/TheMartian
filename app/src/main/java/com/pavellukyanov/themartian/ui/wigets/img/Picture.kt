package com.pavellukyanov.themartian.ui.wigets.img

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.pavellukyanov.themartian.R
import kotlinx.coroutines.Dispatchers

@Composable
fun Picture(
    modifier: Modifier,
    url: Any?,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.None
) {
    val context = LocalContext.current

    val listener = object : ImageRequest.Listener {
        override fun onError(request: ImageRequest, result: ErrorResult) {
            super.onError(request, result)
        }

        override fun onSuccess(request: ImageRequest, result: SuccessResult) {
            super.onSuccess(request, result)
        }
    }

    val imageRequest = ImageRequest.Builder(context)
        .data(url)
        .listener(listener)
        .dispatcher(Dispatchers.IO)
        .memoryCacheKey(url?.toString().orEmpty())
        .diskCacheKey(url?.toString().orEmpty())
        .placeholder(R.drawable.ic_rocket)
        .error(R.drawable.ic_rocket)
        .fallback(R.drawable.ic_rocket)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .build()

    AsyncImage(
        model = imageRequest,
        contentDescription = contentDescription ?: "Изображение",
        modifier = modifier,
        contentScale = contentScale,
    )
}