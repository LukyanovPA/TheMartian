package com.pavellukyanov.themartian.ui.screens.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.pavellukyanov.themartian.R
import com.pavellukyanov.themartian.data.dto.Photo
import com.pavellukyanov.themartian.ui.wigets.dialog.BottomFilter
import com.pavellukyanov.themartian.ui.wigets.img.Picture
import com.pavellukyanov.themartian.ui.wigets.loading.Loading
import com.pavellukyanov.themartian.utils.ext.Launch
import com.pavellukyanov.themartian.utils.ext.asState
import com.pavellukyanov.themartian.utils.ext.itemsPaging
import com.pavellukyanov.themartian.utils.ext.receive
import com.pavellukyanov.themartian.utils.ext.subscribeEffect
import org.koin.androidx.compose.koinViewModel

@Composable
fun GalleryScreen(
    roverName: String,
    isLocal: Boolean,
    modifier: Modifier,
    navController: NavHostController,
    reducer: GalleryReducer = koinViewModel()
) {
    val state by reducer.asState()
    var showBottomSheet by remember { mutableStateOf(false) }

    Launch {
        //isLocal = true -> Избранное
        //isLocal = false -> Из API

        reducer.sendAction(GalleryAction.LoadLatestPhotos(roverName = roverName, isLocal = isLocal))
        reducer.subscribeEffect { effect ->
            when (effect) {
                is GalleryEffect.OnBackClick -> navController.popBackStack()
                is GalleryEffect.OnPhotoClick -> navController.navigate("ui/screens/photo")
            }
        }
    }

    state.receive<GalleryState>(
        content = { currentState ->
            Scaffold(
                modifier = modifier,
                bottomBar = { BottomFilterButton { showBottomSheet = true } }
            ) { padding ->
                GalleryScreenContent(modifier = modifier, paddingValues = padding, state = currentState, onClick = reducer::sendAction)

                if (showBottomSheet) BottomFilter(
                    currentDate = currentState.options.date,
                    isFavourites = isLocal,
                    paddingValues = padding,
                    onShowBottomSheetState = { showBottomSheet = it },
                    onNewDate = {
                        reducer.sendAction(GalleryAction.OnSetNewDate(newDate = it))
                        showBottomSheet = false
                    }
                )
            }
        }
    )
}

@Composable
private fun GalleryScreenContent(
    modifier: Modifier,
    paddingValues: PaddingValues,
    state: GalleryState,
    onClick: (GalleryAction) -> Unit
) {
    val photos: LazyPagingItems<Photo> = state.photos.collectAsLazyPagingItems()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 24.dp, bottom = 25.dp)
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
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                Icon(
                    tint = Color.White,
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.button_back)
                )
            }
            Text(
                modifier = Modifier
                    .weight(3f)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = state.options.roverName,
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
                verticalItemSpacing = 1.dp,
                horizontalArrangement = Arrangement.spacedBy(1.dp),
                content = {
                    itemsPaging(photos) { photo ->
                        Picture(
                            url = photo!!.src,
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

@Composable
private fun BottomFilterButton(
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 25.dp)
            .background(Color.Transparent)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .background(Color.White)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(
                    modifier = Modifier
                        .padding(6.dp)
                        .background(Color.LightGray)
                        .height(2.dp)
                        .width(16.dp)
                )
                Text(
                    modifier = Modifier
                        .padding(horizontal = 6.dp)
                        .padding(bottom = 4.dp),
                    text = stringResource(id = R.string.filter_title),
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}