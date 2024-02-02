package com.pavellukyanov.themartian.ui.screens.photo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.pavellukyanov.themartian.ui.wigets.PhotoScreenContent
import com.pavellukyanov.themartian.utils.ext.Launch
import com.pavellukyanov.themartian.utils.ext.asState
import com.pavellukyanov.themartian.utils.ext.receive
import com.pavellukyanov.themartian.utils.ext.subscribeEffect
import org.koin.androidx.compose.koinViewModel

@Composable
fun PhotoScreen(
    modifier: Modifier,
    navController: NavHostController,
    reducer: PhotoReducer = koinViewModel()
) {
    val state by reducer.asState()

    Launch {
        reducer.sendAction(PhotoAction.LoadPhoto)
        reducer.subscribeEffect { effect ->
            when (effect) {
                is PhotoEffect.OnBackClick -> navController.popBackStack()
            }
        }
    }

    state.receive<PhotoState>(content = { currentState -> PhotoScreenContent(state = currentState, modifier = modifier, onClick = reducer::sendAction) })
}

