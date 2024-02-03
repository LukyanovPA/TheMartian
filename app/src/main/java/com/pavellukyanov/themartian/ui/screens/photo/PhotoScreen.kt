package com.pavellukyanov.themartian.ui.screens.photo

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.pavellukyanov.themartian.R
import com.pavellukyanov.themartian.ui.wigets.img.Picture
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

    state.receive<PhotoState>(
        content = { currentState ->
            PhotoScreenContent(
                photoSrc = currentState.photo?.src.orEmpty(),
                modifier = modifier,
                isFavourites = currentState.isFavourites,
                sol = currentState.photo?.sol?.toString().orEmpty(),
                cameraName = currentState.photo?.cameraName.orEmpty(),
                cameraFullName = currentState.photo?.cameraFullName.orEmpty(),
                earthDate = currentState.photo?.earthDate.orEmpty(),
                roverName = currentState.photo?.roverName.orEmpty(),
                onBackClick = { reducer.sendAction(PhotoAction.OnBackClick) },
                onDownloadClick = { reducer.sendAction(PhotoAction.DownloadPhoto(photo = currentState.photo)) },
                onChangeFavouritesClick = { reducer.sendAction(PhotoAction.ChangeFavourites(photo = currentState.photo)) }
            )
        }
    )
}

@Composable
fun PhotoScreenContent(
    photoSrc: String?,
    modifier: Modifier,
    isFavourites: Boolean,
    sol: String,
    cameraName: String,
    cameraFullName: String,
    earthDate: String,
    roverName: String,
    onBackClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onChangeFavouritesClick: () -> Unit
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var rotationState by remember { mutableFloatStateOf(1f) }

    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
    ) {
        val (buttonBack, photoBox, info) = createRefs()

        //Photo
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .constrainAs(photoBox) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .fillMaxSize() // Give the size you want...
                .pointerInput(Unit) {
                    detectTransformGestures { centroid, pan, zoom, rotation ->
                        scale *= zoom
                        rotationState += rotation
                    }
                }
        ) {
            Picture(
                url = photoSrc,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        // adding some zoom limits (min 50%, max 200%)
                        scaleX = maxOf(0.5f, minOf(3f, scale)),
                        scaleY = maxOf(0.5f, minOf(3f, scale)),
                        rotationZ = rotationState
                    )
            )
        }

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
                    bottom.linkTo(parent.bottom, margin = 30.dp)
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
                        text = sol,
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
                        text = earthDate,
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
                        text = roverName,
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
                        text = "$cameraFullName (${cameraName})",
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