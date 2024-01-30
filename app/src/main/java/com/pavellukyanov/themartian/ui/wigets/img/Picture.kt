package com.pavellukyanov.themartian.ui.wigets.img

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest

@Composable
fun Picture(
    modifier: Modifier,
    url: Any?,
    contentDescription: String? = null
) {
    SubcomposeAsyncImage(
        contentScale = ContentScale.Crop,
        modifier = modifier,
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .crossfade(true)
            .build(),
        loading = {
            CircularProgressIndicator(color = Color.Red)
        },
        contentDescription = contentDescription ?: "Изображение"
    )
}