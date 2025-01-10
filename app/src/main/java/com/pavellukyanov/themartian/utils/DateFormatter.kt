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
                Month.JAN.value -> "Янв"
                Month.FEB.value -> "Фев"
                Month.MAR.value -> "Мар"
                Month.APR.value -> "Апр"
                Month.MAY.value -> "Мая"
                Month.JUN.value -> "Июн"
                Month.JUL.value -> "Июл"
                Month.AUG.value -> "Авг"
                Month.SEP.value -> "Сен"
                Month.OKT.value -> "Окт"
                Month.NOV.value -> "Ноя"
                Month.DEC.value -> "Дек"
                else -> EMPTY_STRING
            }
        else
            when (month) {
                Month.JAN.value -> "Jan"
                Month.FEB.value -> "Feb"
                Month.MAR.value -> "Mar"
                Month.APR.value -> "Apr"
                Month.MAY.value -> "May"
                Month.JUN.value -> "Jun"
                Month.JUL.value -> "Jul"
                Month.AUG.value -> "Aug"
                Month.SEP.value -> "Sep"
                Month.OKT.value -> "Okt"
                Month.NOV.value -> "Nov"
                Month.DEC.value -> "Dec"
                else -> EMPTY_STRING
            }
}

private enum class Month(val value: String) {
    JAN("01"),
    FEB("02"),
    MAR("03"),
    APR("04"),
    MAY("05"),
    JUN("06"),
    JUL("07"),
    AUG("08"),
    SEP("09"),
    OKT("10"),
    NOV("11"),
    DEC("12")
}