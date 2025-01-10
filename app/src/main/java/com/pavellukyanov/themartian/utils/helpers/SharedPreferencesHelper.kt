package com.pavellukyanov.themartian.utils.helpers

import android.content.Context
import com.pavellukyanov.themartian.utils.C
import com.pavellukyanov.themartian.utils.ext.onIo

class SharedPreferencesHelper : Helper() {
    private val prefs = context.getSharedPreferences(C.COMMON, Context.MODE_PRIVATE)

    suspend fun <T> put(key: String, value: T) = onIo {
        with(prefs.edit()) {
            value?.let {
                when (value) {
                    is Boolean -> putBoolean(key, value).apply()
                    is String -> putString(key, value).apply()
                    is Int -> putInt(key, value).apply()
                    is Long -> putLong(key, value).apply()
                    is Float -> putFloat(key, value).apply()
                    else -> throw IllegalStateException("Type $value of property $key is not supported")
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun <T> get(key: String, defaultValue: T): T = onIo {
        with(prefs) {
            when (defaultValue) {
                is Boolean -> getBoolean(key, defaultValue) as T
                is String -> getString(key, defaultValue) as T
                is Int -> getInt(key, defaultValue) as T
                is Long -> getLong(key, defaultValue) as T
                is Float -> getFloat(key, defaultValue) as T
                else -> throw IllegalStateException("Type $defaultValue of property $key is not supported")
            }
        }
    }
}