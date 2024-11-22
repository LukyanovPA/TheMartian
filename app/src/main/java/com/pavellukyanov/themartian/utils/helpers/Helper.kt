package com.pavellukyanov.themartian.utils.helpers

import android.content.Context
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class Helper : KoinComponent {
    protected val context: Context by inject()
}