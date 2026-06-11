package com.pavellukyanov.themartian.ui.wigets.img

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.size.Size as CoilSize
import com.pavellukyanov.themartian.MartianApp
import com.pavellukyanov.themartian.R
import timber.log.Timber
import java.net.SocketTimeoutException

@Composable
fun Picture(
    modifier: Modifier,
    url: Any?,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.None,
    onError: (Throwable) -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as MartianApp
    val safeUrl = url?.toString()?.takeIf { it.isNotBlank() }

    if (safeUrl == null) {
        Image(
            painter = painterResource(id = R.drawable.ic_rocket),
            contentDescription = contentDescription ?: stringResource(id = R.string.default_image_description),
            contentScale = contentScale,
            modifier = modifier.fillMaxSize()
        )
        return
    }

    val listener = object : ImageRequest.Listener {
        override fun onError(request: ImageRequest, result: ErrorResult) {
            super.onError(request, result)
            Timber.e(result.throwable)
            val isSuppressed = result.throwable is IllegalStateException
                && result.throwable.message?.contains("Unable to create a fetcher that supports", ignoreCase = true) == true
            val isTimeout = result.throwable is SocketTimeoutException
            if (!isSuppressed && !isTimeout) {
                onError(result.throwable)
            }
        }
    }

    val imageRequest = ImageRequest.Builder(LocalContext.current)
        .data(safeUrl)
        .size(CoilSize.ORIGINAL)
        .listener(listener)
        .memoryCacheKey(safeUrl)
        .diskCacheKey(safeUrl)
        .build()

    SubcomposeAsyncImage(
        model = imageRequest,
        imageLoader = app.imageLoader,
        modifier = modifier,
        contentDescription = contentDescription ?: stringResource(id = R.string.default_image_description),
        contentScale = contentScale
    )
}
