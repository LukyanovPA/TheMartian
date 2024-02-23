package com.pavellukyanov.themartian.ui.screens.photo

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.pavellukyanov.themartian.R
import com.pavellukyanov.themartian.data.dto.Photo
import com.pavellukyanov.themartian.ui.wigets.img.Picture
import com.pavellukyanov.themartian.utils.ext.Launch
import com.pavellukyanov.themartian.utils.ext.asState
import com.pavellukyanov.themartian.utils.ext.receive
import com.pavellukyanov.themartian.utils.ext.subscribeEffect
import org.koin.androidx.compose.koinViewModel

@Composable
fun PhotoScreen(
    photoId: Int,
    modifier: Modifier,
    navController: NavHostController,
    reducer: PhotoReducer = koinViewModel()
) {
    val state by reducer.asState()

    Launch {
        reducer.sendAction(PhotoAction.LoadPhoto(photoId = photoId))
        reducer.subscribeEffect { effect ->
            when (effect) {
                is PhotoEffect.OnBackClick -> navController.popBackStack()
            }
        }
    }

    state.receive<PhotoState>(
        content = { currentState ->
            PhotoScreenContent(
                modifier = modifier,
                isFavourites = currentState.isFavourites,
                photo = currentState.photo,
                onBackClick = { reducer.sendAction(PhotoAction.OnBackClick) },
                onDownloadClick = { reducer.sendAction(PhotoAction.DownloadPhoto(photo = currentState.photo)) },
                onChangeFavouritesClick = { reducer.sendAction(PhotoAction.ChangeFavourites(photo = currentState.photo)) },
                onError = { reducer.sendAction(PhotoAction.OnImageError(error = it)) }
            )
        }
    )
}

@Composable
private fun PhotoScreenContent(
    modifier: Modifier,
    isFavourites: Boolean,
    photo: Photo?,
    onBackClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onChangeFavouritesClick: () -> Unit,
    onError: (Throwable) -> Unit
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var rotation by remember { mutableFloatStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale *= zoomChange
        rotation += rotationChange
        offset += offsetChange
    }

    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
    ) {
        val (buttonBack, photoBox, info) = createRefs()

        //Photo
        Picture(
            url = photo?.src.orEmpty(),
            contentDescription = null,
            modifier = Modifier
                .constrainAs(photoBox) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .fillMaxWidth()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    rotationZ = rotation,
                    translationX = offset.x,
                    translationY = offset.y
                )
                .transformable(state = state),
            onError = onError
        )

        //Header
        Column(
            modifier = Modifier
                .constrainAs(buttonBack) {
                    top.linkTo(parent.top, margin = 40.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                }
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .size(40.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                //Button Back
                Button(
                    modifier = Modifier
                        .size(40.dp),
                    onClick = onBackClick,
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

                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                ) {
                    //Button Download
                    Button(
                        modifier = Modifier
                            .size(40.dp),
                        onClick = onDownloadClick,
                        shape = CircleShape,
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                    ) {
                        Icon(
                            tint = Color.White,
                            modifier = Modifier
                                .padding(4.dp),
                            painter = painterResource(id = R.drawable.ic_download),
                            contentDescription = stringResource(id = R.string.button_back)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    //Button Favourites
                    Button(
                        modifier = Modifier
                            .size(40.dp),
                        onClick = onChangeFavouritesClick,
                        shape = CircleShape,
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                    ) {
                        Icon(
                            tint = Color.Yellow,
                            modifier = Modifier
                                .padding(4.dp),
                            painter = if (isFavourites) painterResource(id = R.drawable.ic_favourites) else painterResource(id = R.drawable.ic_is_not_favourites),
                            contentDescription = stringResource(id = R.string.button_back)
                        )
                    }
                }
            }
        }

        //Info
        Box(
            modifier = Modifier.run {
                constrainAs(info) {
                    bottom.linkTo(parent.bottom, margin = 36.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                }
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .background(color = Color.LightGray.copy(alpha = 0.3f), shape = RoundedCornerShape(16.dp))
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                //Sol
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        text = stringResource(id = R.string.sol),
                        color = Color.White,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.Start
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = photo?.sol?.toString().orEmpty(),
                        color = Color.White,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.Start
                    )
                }
                //Earth Date
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        text = stringResource(id = R.string.earth_date),
                        color = Color.White,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.Start
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = photo?.earthFormattedDate.orEmpty(),
                        color = Color.White,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.Start
                    )
                }
                //Rover
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        text = stringResource(id = R.string.rover_name),
                        color = Color.White,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.Start
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = photo?.roverName.orEmpty(),
                        color = Color.White,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.Start
                    )
                }
                //Camera
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        text = stringResource(id = R.string.camera_name),
                        color = Color.White,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.Start
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "${photo?.cameraFullName.orEmpty()} (${photo?.cameraName.orEmpty()})",
                        color = Color.White,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.Start
                    )
                }
            }
        }
    }
}