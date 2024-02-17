package com.pavellukyanov.themartian.domain.entity

import androidx.compose.ui.graphics.Color

data class CacheItem(
    val sizeMb: Long,
    val chartValue: Float,
    val chartColor: Color,
    val title: Int
)
