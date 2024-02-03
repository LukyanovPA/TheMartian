package com.pavellukyanov.themartian.utils.ext

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridItemScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import com.pavellukyanov.themartian.ui.base.Effect
import com.pavellukyanov.themartian.ui.base.Reducer
import com.pavellukyanov.themartian.ui.base.State
import com.pavellukyanov.themartian.ui.wigets.loading.Loading
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.receiveAsFlow
import com.pavellukyanov.themartian.ui.base.State as UiState

@Composable
internal fun <STATE : State> Reducer<STATE, *, *>.asState(): androidx.compose.runtime.State<UiState> =
    state.collectAsStateWithLifecycle(initialValue = UiState())

@Composable
internal inline fun <reified STATE : UiState> UiState.receive(
    modifier: Modifier,
    crossinline content: @Composable (currentState: STATE) -> Unit
) {
    when (this) {
        is STATE -> if (isLoading) Loading(modifier = modifier) else content(this)
    }
}

@Composable
internal inline fun <reified STATE : UiState> UiState.receive(
    crossinline content: @Composable (currentState: STATE) -> Unit
) {
    when (this) {
        is STATE -> content(this)
    }
}

@Composable
internal fun Launch(block: suspend CoroutineScope.() -> Unit) {
    LaunchedEffect(key1 = true, block = block)
}

/** Observe EFFECT */
suspend fun <EFFECT : Effect> Reducer<*, *, EFFECT>.subscribeEffect(onEffect: (effect: EFFECT) -> Unit) {
    effect.receiveAsFlow()
        .collect(onEffect)
}

fun <T : Any> LazyStaggeredGridScope.itemsPaging(
    items: LazyPagingItems<T>,
    key: ((item: T) -> Any)? = null,
    itemContent: @Composable LazyStaggeredGridItemScope.(value: T?) -> Unit
) {
    items(
        count = items.itemCount,
        key = if (key == null) null else { index ->
            val item = items.peek(index)
            if (item == null) {
                PagingPlaceholderKey(index)
            } else {
                key(item)
            }
        }
    ) { index ->
        itemContent(items[index])
    }
}


@SuppressLint("BanParcelableUsage")
private data class PagingPlaceholderKey(private val index: Int) : Parcelable {

    override fun writeToParcel(parcel: Parcel, flags: Int) = parcel.writeInt(index)

    override fun describeContents(): Int = 0

    companion object {
        @Suppress("unused")
        @JvmField
        val CREATOR: Parcelable.Creator<PagingPlaceholderKey> =
            object : Parcelable.Creator<PagingPlaceholderKey> {
                override fun createFromParcel(parcel: Parcel) =
                    PagingPlaceholderKey(parcel.readInt())

                override fun newArray(size: Int) = arrayOfNulls<PagingPlaceholderKey?>(size)
            }
    }
}