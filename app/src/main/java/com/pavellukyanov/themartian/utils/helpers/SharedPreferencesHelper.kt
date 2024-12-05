package com.pavellukyanov.themartian.utils.helpers

import android.content.Context
import com.pavellukyanov.themartian.utils.C

class SharedPreferencesHelper : Helper() {
    private val prefs = context.getSharedPreferences(C.COMMON, Context.MODE_PRIVATE)

    fun putFloat(key: String, value: Float) = prefs.edit().putFloat(key, value).apply()

    fun getFloat(key: String, defaultValue: Float) = prefs.getFloat(key, defaultValue)
}