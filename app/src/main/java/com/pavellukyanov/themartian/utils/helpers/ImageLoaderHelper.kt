package com.pavellukyanov.themartian.utils.helpers

import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import com.pavellukyanov.themartian.MartianApp

class ImageLoaderHelper : Helper() {
    private val app get() = context.applicationContext as MartianApp

    fun getDiskCache(): DiskCache? = app.imageLoader.diskCache

    fun getMemoryCache(): MemoryCache? = app.imageLoader.memoryCache
}
