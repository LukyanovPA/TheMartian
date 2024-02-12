package com.pavellukyanov.themartian.utils

import com.pavellukyanov.themartian.utils.C.DASH
import com.pavellukyanov.themartian.utils.C.EMPTY_STRING
import com.pavellukyanov.themartian.utils.C.INT_ZERO
import com.pavellukyanov.themartian.utils.C.RU
import com.pavellukyanov.themartian.utils.C.SPACE
import java.util.Locale

object DateFormatter {

    fun format(str: String): String {
        val year = str.substringBefore(DASH)

        val month = listOf(
            str[5],
            str[6]
        ).joinToString(separator = EMPTY_STRING)

        val day = listOf(
            SPACE,
            str[8],
            str[9],
        ).joinToString(separator = EMPTY_STRING)

        return listOf(year, SPACE, getMonth(month), day).joinToString(separator = EMPTY_STRING)
    }

    //год, месяц, день
    fun parse(date: String): Triple<Int, Int, Int> =
        if (date.isNotEmpty()) {
            val list = date.split('-')
            Triple(list.first().toInt(), list[1].toInt(), list[2].toInt())
        } else {
            Triple(INT_ZERO, INT_ZERO, INT_ZERO)
        }

    private fun getMonth(month: String): String =
        if (Locale.getDefault().language == RU)
            when (month) {
                "01" -> "Янв"
                "02" -> "Фев"
                "03" -> "Мар"
                "04" -> "Апр"
                "05" -> "Мая"
                "06" -> "Июн"
                "07" -> "Июл"
                "08" -> "Авг"
                "09" -> "Сен"
                "10" -> "Окт"
                "11" -> "Ноя"
                "12" -> "Дек"
                else -> EMPTY_STRING
            }
        else
            when (month) {
                "01" -> "Jan"
                "02" -> "Feb"
                "03" -> "Mar"
                "04" -> "Apr"
                "05" -> "May"
                "06" -> "Jun"
                "07" -> "Jul"
                "08" -> "Aug"
                "09" -> "Sep"
                "10" -> "Okt"
                "11" -> "Nov"
                "12" -> "Dec"
                else -> EMPTY_STRING
            }
}