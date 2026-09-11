package com.pavellukyanov.themartian.utils

/**
 * The Photo viewer needs to know its neighbours in the list it was opened from — position,
 * total, and which photo id is prev/next — but each screen owns its own scoped ViewModel, so
 * there is no direct line from [com.pavellukyanov.themartian.ui.screens.photo.PhotoReducer]
 * back to [com.pavellukyanov.themartian.ui.screens.gallery.GalleryReducer]'s state.
 *
 * Rather than thread the whole photo list through navigation args (fine for a page of 25, ugly
 * once pagination has loaded a few hundred) or persist the browsed list to the DB (that table
 * is for favourites/cache, not "whatever the user happened to scroll past"), this is a small
 * in-memory, session-scoped lookup: GalleryReducer keeps it in sync with whatever list is
 * currently on screen, PhotoReducer reads a snapshot when a photo loads.
 *
 * It is deliberately best-effort: a photo opened without this ever having been populated (a
 * deep link, a cold process restart landing back on Photo) just has no neighbours — no crash,
 * no counter, no arrows, exactly like before this feature existed.
 */
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
