package com.pavellukyanov.themartian.ui.wigets.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pavellukyanov.themartian.R
import com.pavellukyanov.themartian.domain.entity.CacheItem

@Composable
fun CircularChart(
    items: List<CacheItem>,
    currentSize: Float,
    backgroundCircleColor: Color = Color.LightGray.copy(alpha = 0.3f),
    size: Dp = 280.dp,
    thickness: Dp = 16.dp,
    gapBetweenCircles: Dp = 42.dp
) {
    val sweepAngles = items.map { item ->
        360 * if (item.chartValue <= 0) 0.01f else item.chartValue / currentSize
    }

    Canvas(
        modifier = Modifier
            .size(size)
    ) {
        var arcRadius = size.toPx()

        items.forEachIndexed { index, cacheItem ->
            arcRadius -= gapBetweenCircles.toPx()

            drawCircle(
                color = backgroundCircleColor,
                radius = arcRadius / 2,
                style = Stroke(width = thickness.toPx(), cap = StrokeCap.Butt)
            )

            drawArc(
                color = if (sweepAngles[index] > 360) Color.Red else cacheItem.chartColor,
                startAngle = -90f,
                sweepAngle = sweepAngles[index],
                useCenter = false,
                style = Stroke(width = thickness.toPx(), cap = StrokeCap.Round),
                size = Size(arcRadius, arcRadius),
                topLeft = Offset(
                    x = (size.toPx() - arcRadius) / 2,
                    y = (size.toPx() - arcRadius) / 2
                )
            )
        }
    }

    Spacer(modifier = Modifier.height(32.dp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        items.forEach { item ->
            DisplayLegend(color = item.chartColor, title = item.title, size = item.sizeMb)
        }
    }
}

@Composable
fun DisplayLegend(
    color: Color,
    title: Int,
    size: Long
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(color = color, shape = CircleShape)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = stringResource(id = title),
                color = Color.Black
            )
        }
        Row(
            modifier = Modifier
                .weight(1f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (size <= 0) stringResource(id = R.string.cache_size_mb_small) else stringResource(id = R.string.cache_size_mb, size),
                fontWeight = FontWeight.Medium,
                color = Color.Blue
            )
        }
    }
}