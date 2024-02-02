package com.pavellukyanov.themartian.ui.screens.gallery

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.pavellukyanov.themartian.R
import com.pavellukyanov.themartian.ui.wigets.img.Picture
import com.pavellukyanov.themartian.ui.wigets.loading.Loading
import com.pavellukyanov.themartian.utils.ext.Launch
import com.pavellukyanov.themartian.utils.ext.asState
import com.pavellukyanov.themartian.utils.ext.receive
import com.pavellukyanov.themartian.utils.ext.subscribeEffect
import org.koin.androidx.compose.koinViewModel

@Composable
fun GalleryScreen(
    roverName: String,
    modifier: Modifier,
    navController: NavHostController,
    reducer: GalleryReducer = koinViewModel()
) {
    val state by reducer.asState()

    Launch {
        reducer.sendAction(GalleryAction.LoadLatestPhotos(roverName = roverName))
        reducer.subscribeEffect { effect ->
            when (effect) {
                is GalleryEffect.OnBackClick -> navController.popBackStack()
                is GalleryEffect.OnPhotoClick -> navController.navigate("ui/screens/photo")
            }
        }
    }

    state.receive<GalleryState>(
        content = { currentState ->
            GalleryScreenContent(modifier = modifier, state = currentState, onClick = reducer::sendAction)
        }
    )
}

@Composable
private fun GalleryScreenContent(
    modifier: Modifier,
    state: GalleryState,
    onClick: (GalleryAction) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 24.dp, bottom = 30.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Button(
                modifier = Modifier
                    .size(40.dp),
                onClick = { onClick(GalleryAction.OnBackClick) },
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.button_back)
                )
            }
            Text(
                modifier = Modifier
                    .weight(3f)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = state.roverName,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            )
        }
        if (state.isLoading) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Loading(modifier = Modifier)
            }
        } else {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                verticalItemSpacing = 4.dp,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                content = {
                    items(state.photos) { photo ->
                        Picture(
                            url = photo.imgSrc,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .clickable { onClick(GalleryAction.OnPhotoClick(photo)) }
                        )
                    }
                }
            )
        }
    }
}