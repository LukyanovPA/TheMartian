package com.pavellukyanov.themartian.domain.entity

import com.pavellukyanov.themartian.utils.C.EMPTY_STRING
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PhotosOptions(
    val roverName: String = EMPTY_STRING,
    val date: String = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(Date(System.currentTimeMillis())),
    val displayDate: String = SimpleDateFormat("yyyy MM dd", Locale.ROOT).format(Date(System.currentTimeMillis())),
    val camera: String? = null,
)