package com.pavellukyanov.themartian.utils

class GalleryBrowseSession {
    @Volatile
    private var ids: List<Int> = emptyList()

    fun update(photoIds: List<Int>) {
        ids = photoIds
    }

    fun contextFor(photoId: Int): BrowseContext? {
        val index = ids.indexOf(photoId)
        if (index == -1) return null
        return BrowseContext(
            position = index + 1,
            total = ids.size,
            previousId = ids.getOrNull(index - 1),
            nextId = ids.getOrNull(index + 1)
        )
    }
}

data class BrowseContext(
    val position: Int,
    val total: Int,
    val previousId: Int?,
    val nextId: Int?
)
