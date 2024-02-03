package com.pavellukyanov.themartian.utils

object DateFormatter {
    private const val DEFAULT = ""
    private const val SPACE = " "
    private const val DASH = "-"

    fun format(str: String): String {
        val year = str.substringBefore(DASH)

        val month = listOf(
            str[5],
            str[6]
        ).joinToString(separator = DEFAULT)

        val day = listOf(
            SPACE,
            str[8],
            str[9],
        ).joinToString(separator = DEFAULT)

        return listOf(year, SPACE, getMonth(month), day).joinToString(separator = DEFAULT)
    }

    //год, месяц, день
    fun parse(date: String): Triple<Int, Int, Int> =
        if (date.isNotEmpty()) {
            val list = date.split(' ')
            Triple(list.first().toInt(), getMonthInt(list[1]), list[2].toInt())
        } else {
            Triple(0, 0, 0)
        }

    private fun getMonth(month: String): String =
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
            else -> DEFAULT
        }

    private fun getMonthInt(month: String): Int =
        when (month) {
            "Jan" -> 0
            "Feb" -> 1
            "Mar" -> 2
            "Apr" -> 3
            "May" -> 4
            "Jun" -> 5
            "Jul" -> 6
            "Aug" -> 7
            "Sep" -> 8
            "Okt" -> 9
            "Nov" -> 10
            "Dec" -> 11
            else -> 0
        }
}