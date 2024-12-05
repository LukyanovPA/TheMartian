package com.pavellukyanov.themartian.ui.wigets.img

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ErrorResult
import coil.request.ImageRequest
import com.pavellukyanov.themartian.R
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import coil.size.Size as CoilSize

@Composable
fun Picture(
    modifier: Modifier,
    url: Any?,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.None,
    onError: (Throwable) -> Unit
) {
    val listener = object : ImageRequest.Listener {
        override fun onError(request: ImageRequest, result: ErrorResult) {
            super.onError(request, result)
            Timber.e(result.throwable)
            if (result.throwable is IllegalStateException) {
                if (result.throwable.message?.contains("Unable to create a fetcher that supports", ignoreCase = true) == false) onError(result.throwable)
            } else {
                onError(result.throwable)
            }
        }
    }

    val imageRequest = ImageRequest.Builder(LocalContext.current)
        .data(url)
        .size(CoilSize.ORIGINAL)
        .crossfade(true)
        .listener(listener)
        .dispatcher(Dispatchers.IO)
        .memoryCacheKey(url?.toString().orEmpty())
        .diskCacheKey(url?.toString().orEmpty())
        .error(R.drawable.ic_rocket)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .fallback(R.drawable.ic_rocket)
        .build()

    SubcomposeAsyncImage(
        model = imageRequest,
        loading = {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(8.dp)
                    .fillMaxSize()
                    .padding(60.dp),
                color = Color.Red.copy(alpha = 0.7f)
            )
        },
        contentDescription = contentDescription ?: stringResource(id = R.string.default_image_description),
        contentScale = contentScale,
        modifier = modifier
    )
}