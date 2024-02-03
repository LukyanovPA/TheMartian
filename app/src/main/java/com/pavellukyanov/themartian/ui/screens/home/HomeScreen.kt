package com.pavellukyanov.themartian.ui.screens.home

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.pavellukyanov.themartian.utils.ext.Launch
import com.pavellukyanov.themartian.utils.ext.asState
import com.pavellukyanov.themartian.utils.ext.receive
import com.pavellukyanov.themartian.utils.ext.subscribeEffect
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    modifier: Modifier,
    navController: NavHostController,
    reducer: HomeReducer = koinViewModel()
) {
    val state by reducer.asState()

    Launch {
        reducer.sendAction(HomeAction.LoadRovers)
        reducer.subscribeEffect { effect ->
            when (effect) {
                is HomeEffect.NavigateToRoverGallery -> navController.navigate("ui/screens/gallery/${effect.roverName}/${false}")
            }
        }
    }

    state.receive<HomeState>(
        modifier = modifier,
        content = { currentState ->
            HomeScreenContent(
                state = currentState,
                modifier = modifier,
                onClick = reducer::sendAction
            )
        }
    )
}

@Composable
private fun HomeScreenContent(
    modifier: Modifier,
    state: HomeState,
    onClick: (HomeAction) -> Unit
) {
    LazyColumn(
        state = rememberLazyListState(),
        modifier = modifier.padding(vertical = 30.dp)
    ) {
        //Rovers
        state.rovers.forEach { rover ->
            item {
                rover.Content(onClick = { onClick(HomeAction.OnRoverClick(rover = it)) })
            }
        }
    }
}