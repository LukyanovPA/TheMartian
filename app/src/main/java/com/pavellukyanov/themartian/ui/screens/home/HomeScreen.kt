package com.pavellukyanov.themartian.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.pavellukyanov.themartian.ui.Launch
import com.pavellukyanov.themartian.ui.asUiState
import com.pavellukyanov.themartian.ui.receiveWithPadding
import com.pavellukyanov.themartian.ui.wigets.img.Picture
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    navController: NavHostController,
    paddingValues: PaddingValues,
    reducer: HomeReducer = koinViewModel()
) {
    val state by reducer.state.asUiState()

    Launch {
        reducer.sendAction(HomeAction.LoadRovers)
    }

    state.receiveWithPadding<HomeState>(
        padding = paddingValues,
        content = { currentState ->
            HomeScreenContent(
                state = currentState
            )
        }
    )
}

@Composable
private fun HomeScreenContent(
    state: HomeState
) {
    LazyColumn(
        state = rememberLazyListState(),
        modifier = Modifier
            .fillMaxSize()
    ) {
        //Rovers
        state.rovers.forEach { rover ->
            item {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                    ) {
                        Text(text = rover.roverName)
                    }
                    Picture(modifier = Modifier.fillMaxSize(), url = rover.roverImage)
                }
            }
        }
    }
}