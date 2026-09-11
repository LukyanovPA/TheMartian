package com.pavellukyanov.themartian.ui.wigets.loading

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pavellukyanov.themartian.R
import com.pavellukyanov.themartian.ui.theme.AccentMars
import com.pavellukyanov.themartian.ui.theme.GolosFontFamily
import com.pavellukyanov.themartian.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun Loading(
    modifier: Modifier,
    circleColor: Color = AccentMars,
    circleSize: Dp = 18.dp,
    animationDelay: Int = 400,
    initialAlpha: Float = 0.3f
) {
    val circles = listOf(
        remember {
            Animatable(initialValue = initialAlpha)
        },
        remember {
            Animatable(initialValue = initialAlpha)
        },
        remember {
            Animatable(initialValue = initialAlpha)
        }
    )

    circles.forEachIndexed { index, animatable ->
        LaunchedEffect(Unit) {
            delay(timeMillis = (animationDelay / circles.size).toLong() * index)
            animatable.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = animationDelay
                    ),
                    repeatMode = RepeatMode.Reverse
                )
            )
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
            ) {
                circles.forEachIndexed { index, animatable ->
                    if (index != 0) {
                        Spacer(modifier = Modifier.width(width = 6.dp))
                    }
                    Box(
                        modifier = Modifier
                            .size(size = circleSize)
                            .clip(shape = CircleShape)
                            .background(
                                color = circleColor
                                    .copy(alpha = animatable.value)
                            )
                    ) {
                    }
                }
            }
            Text(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(),
                text = stringResource(R.string.loading_title),
                fontWeight = FontWeight.Normal,
                color = TextSecondary,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                fontFamily = GolosFontFamily
            )
        }
    }
}
