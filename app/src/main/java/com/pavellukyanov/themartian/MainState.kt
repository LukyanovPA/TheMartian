package com.pavellukyanov.themartian

import com.pavellukyanov.themartian.domain.entity.CacheItem
import com.pavellukyanov.themartian.services.CacheServiceLifecycle
import com.pavellukyanov.themartian.ui.base.Action
import com.pavellukyanov.themartian.ui.base.Effect
import com.pavellukyanov.themartian.ui.base.State

data class MainState(
    override val isLoading: Boolean = false,
    val cacheItems: List<CacheItem> = listOf(),
    val currentCacheSize: Float = 0f,
    val cacheServiceStatus: CacheServiceLifecycle = CacheServiceLifecycle.DIDNT_START
) : State()

sealed class MainAction : Action() {
    data class Error(val error: Throwable) : MainAction()
    data object CloseErrorDialog : MainAction()
    data object OnDeleteCache : MainAction()
    data object OnUpdateSettings : MainAction()
    data class OnCacheSizeChange(val size: Float) : MainAction()
    data object CheckCacheOverSize : MainAction()
    data object OnFavouritesClick : MainAction()
    data class OnCacheServiceStatus(val cacheServiceStatus: CacheServiceLifecycle) : MainAction()
}

sealed class MainEffect : Effect() {
    data class ShowError(val errorMessage: String) : MainEffect()
    data object CloseErrorDialog : MainEffect()
    data object OpenFavourites : MainEffect()
}