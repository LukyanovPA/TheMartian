package com.pavellukyanov.themartian.utils.helpers

import coil.disk.DiskCache
import coil.imageLoader
import coil.memory.MemoryCache

class ImageLoaderHelper : Helper() {
    fun getDiskCache(): DiskCache? = context.imageLoader.diskCache

    fun getMemoryCache(): MemoryCache? = context.imageLoader.memoryCache
}