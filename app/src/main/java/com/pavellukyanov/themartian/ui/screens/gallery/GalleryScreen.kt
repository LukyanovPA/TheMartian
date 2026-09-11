package com.pavellukyanov.themartian.ui.screens.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.pavellukyanov.themartian.R
import com.pavellukyanov.themartian.data.dto.Photo
import com.pavellukyanov.themartian.ui.theme.AccentMars
import com.pavellukyanov.themartian.ui.theme.AccentMarsLight
import com.pavellukyanov.themartian.ui.theme.BgDeep
import com.pavellukyanov.themartian.ui.theme.MartianType
import com.pavellukyanov.themartian.ui.theme.TextPrimary
import com.pavellukyanov.themartian.ui.theme.TextTertiary
import com.pavellukyanov.themartian.ui.wigets.EmptyResponse
import com.pavellukyanov.themartian.ui.wigets.dialog.BottomFilter
import com.pavellukyanov.themartian.ui.wigets.img.Picture
import com.pavellukyanov.themartian.ui.wigets.loading.Loading
import com.pavellukyanov.themartian.ui.wigets.nav.BottomNavTab
import com.pavellukyanov.themartian.ui.wigets.nav.MartianBottomNav
import com.pavellukyanov.themartian.utils.ext.asState
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
    val gridState = rememberLazyStaggeredGridState()

    // Initial load (cameras included)
    LaunchedEffect(roverName, isLocal) {
        reducer.dispatch(GalleryAction.InitGallery(roverName = roverName, isLocal = isLocal))
    }

    // Effects
    LaunchedEffect(Unit) {
        reducer.subscribeEffect { effect ->
            when (effect) {
                is GalleryEffect.OnBackClick -> navController.popBackStack()
                is GalleryEffect.OnPhotoClick -> navController.navigate("ui/screens/photo/${effect.photoId}")
            }
        }
    }

    // Scroll-based pagination — emits on every item count change (new page loaded)
    LaunchedEffect(gridState) {
        snapshotFlow { gridState.layoutInfo.totalItemsCount }
            .collect {
                val lastVisible = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: return@collect
                val currentState = state as? GalleryState ?: return@collect
                if (it > 0 && lastVisible >= it - 3 && currentState.canPaginate && !currentState.isLoading) {
                    reducer.dispatch(GalleryAction.LoadPage(currentState.page))
                }
            }
    }

    state.receive<GalleryState>(
        content = { currentState ->
            Box(modifier = modifier.fillMaxSize()) {
                GalleryScreenContent(
                    modifier = Modifier,
                    isLocal = currentState.isLocal,
                    state = currentState,
                    gridState = gridState,
                    navController = navController,
                    onAction = reducer::dispatch,
                    onFilterClick = { showBottomSheet = true }
                )

                if (showBottomSheet) BottomFilter(
                    rovers = currentState.rovers,
                    chooseRover = currentState.chooseRover,
                    cameras = currentState.cameras,
                    options = currentState.options,
                    isFavourites = isLocal,
                    onShowBottomSheetState = { showBottomSheet = it },
                    onNewOptions = {
                        reducer.dispatch(GalleryAction.OnSetNewOptions(newOptions = it))
                        showBottomSheet = false
                    },
                    onChooseRover = {
                        reducer.dispatch(GalleryAction.OnChooseRover(rover = it))
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
    isLocal: Boolean,
    state: GalleryState,
    gridState: LazyStaggeredGridState,
    navController: NavHostController,
    onAction: (GalleryAction) -> Unit,
    onFilterClick: () -> Unit
) {
    // The staggered grid and the FAB both live in a `weight(1f)` Box below — Column already
    // measures the (fixed-height) bottom nav first and gives the Box only the space left over,
    // so their own bottom clearance never needs to account for whether the nav bar is showing.
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgDeep)
            .padding(top = 20.dp)
    ) {
        //Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Button(
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.CenterStart),
                onClick = { onAction(GalleryAction.OnBackClick) },
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                Icon(
                    tint = TextPrimary,
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.button_back)
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (state.isLocal) stringResource(id = R.string.favourites_title) else state.options.roverName,
                    style = MartianType.CardTitle,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
                val count = state.totalCount ?: state.photos.size
                if (count > 0 || state.isLocal) {
                    Text(
                        modifier = Modifier.padding(top = 2.dp),
                        text = stringResource(id = R.string.gallery_photos_count, count),
                        style = MartianType.MonoCaption,
                        color = TextTertiary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        //Active filter summary
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 10.dp)
        ) {
            if (isLocal) {
                FilterChip(text = state.chooseRover ?: stringResource(id = R.string.filter_camera_dialog_all), onClick = onFilterClick)
            } else {
                FilterChip(text = state.options.displayDate, onClick = onFilterClick)
                FilterChip(text = state.options.camera ?: stringResource(id = R.string.filter_camera_dialog_all), onClick = onFilterClick)
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            if (state.isLoading && state.photos.isEmpty()) {
                Loading(modifier = Modifier.fillMaxSize())
            } else if (state.photos.isEmpty()) {
                EmptyResponse(modifier = Modifier.fillMaxSize(), isFavourites = isLocal)
            } else {
                LazyVerticalStaggeredGrid(
                    state = gridState,
                    columns = StaggeredGridCells.Fixed(2),
                    verticalItemSpacing = 3.dp,
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    contentPadding = PaddingValues(start = 3.dp, end = 3.dp, bottom = 88.dp),
                    content = {
                        items(state.photos.size) { index ->
                            val photo = state.photos[index]
                            PhotoTile(
                                modifier = Modifier.animateItem(),
                                photo = photo,
                                onClick = { onAction(GalleryAction.OnPhotoClick(photo)) },
                                onError = { onAction(GalleryAction.OnImageError(error = it)) }
                            )
                        }
                    }
                )
            }

            FilterFab(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 16.dp),
                onClick = onFilterClick
            )
        }

        if (isLocal) {
            MartianBottomNav(
                selected = BottomNavTab.FAVOURITES,
                onRoversClick = {
                    navController.navigate("ui/screens/home") {
                        popUpTo("ui/screens/home") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onFavouritesClick = {}
            )
        }
    }
}

@Composable
private fun PhotoTile(
    modifier: Modifier,
    photo: Photo,
    onClick: () -> Unit,
    onError: (Throwable) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable(onClick = onClick)
    ) {
        Picture(
            url = photo.src,
            contentDescription = photo.cameraFullName,
            modifier = Modifier.fillMaxWidth(),
            onError = onError
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, BgDeep.copy(alpha = 0.85f))
                    )
                )
                .padding(start = 8.dp, end = 8.dp, top = 18.dp, bottom = 6.dp)
        ) {
            Text(
                text = "${photo.cameraName} · ${photo.sol}",
                style = MartianType.MonoCaption,
                color = TextPrimary.copy(alpha = 0.92f)
            )
        }
    }
}

@Composable
private fun FilterChip(
    text: String,
    onClick: () -> Unit
) {
    Text(
        modifier = Modifier
            .background(color = AccentMars.copy(alpha = 0.14f), shape = RoundedCornerShape(999.dp))
            .border(width = 1.dp, color = AccentMars.copy(alpha = 0.5f), shape = RoundedCornerShape(999.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 13.dp, vertical = 7.dp),
        text = text,
        style = MartianType.MonoTag,
        color = AccentMarsLight
    )
}

@Composable
private fun FilterFab(
    modifier: Modifier,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable(onClick = onClick)
            .background(color = AccentMars, shape = RoundedCornerShape(999.dp))
            .padding(horizontal = 18.dp, vertical = 13.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Tune,
            contentDescription = stringResource(id = R.string.filter_button_description),
            tint = BgDeep,
            modifier = Modifier.size(18.dp)
        )
        Text(
            modifier = Modifier.padding(start = 9.dp),
            text = stringResource(id = R.string.filter_title),
            style = MartianType.Body,
            color = BgDeep
        )
    }
}
