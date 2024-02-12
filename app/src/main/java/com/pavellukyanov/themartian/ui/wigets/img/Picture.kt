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
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.size.Size
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
        .size(Size.ORIGINAL)
        .crossfade(true)
        .listener(listener)
        .dispatcher(Dispatchers.IO)
        .memoryCacheKey(url?.toString().orEmpty())
        .diskCacheKey(url?.toString().orEmpty())
        .error(R.drawable.ic_rocket)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
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
        contentDescription = contentDescription ?: "Изображение",
        contentScale = contentScale,
        modifier = modifier
    )
}