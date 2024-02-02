package com.pavellukyanov.themartian.ui.wigets

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
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.pavellukyanov.themartian.R
import com.pavellukyanov.themartian.ui.base.Action
import com.pavellukyanov.themartian.ui.screens.photo.PhotoAction
import com.pavellukyanov.themartian.ui.wigets.img.Picture

@Composable
fun <T : Action> PhotoScreenContent(
    photoSrc: String?,
    modifier: Modifier,
    onClick: (T) -> Unit
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
                    onClick = { onClick(PhotoAction.OnBackClick) },
                    shape = CircleShape,
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
                ) {
                    Icon(
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
                        onClick = {
                            state.photoDto?.let { onClick(PhotoAction.DownloadPhoto(photo = it)) }
                        },
                        shape = CircleShape,
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
                    ) {
                        Icon(
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
                        onClick = { },
                        shape = CircleShape,
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
                    ) {
                        Icon(
                            modifier = Modifier
                                .padding(4.dp),
                            painter = if (state.isFavourites) painterResource(id = R.drawable.ic_favourites) else painterResource(id = R.drawable.ic_is_not_favourites),
                            contentDescription = stringResource(id = R.string.button_back)
                        )
                    }
                }
            }
        }

        //Info
        Box(
            modifier = Modifier
                .constrainAs(info) {
                    bottom.linkTo(parent.bottom, margin = 30.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                }
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .background(color = Color.LightGray.copy(alpha = 0.3f), shape = RoundedCornerShape(16.dp))
        ) {
            state.photoDto?.let { photo ->
                Column(
                    modifier = Modifier.padding(16.dp)
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
                            modifier = Modifier,
                            text = photo.sol.toString(),
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
                            modifier = Modifier,
                            text = photo.earthDate,
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
                            modifier = Modifier,
                            text = photo.roverDto.name,
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
                            modifier = Modifier,
                            text = "${photo.cameraDto.fullName} (${photo.cameraDto.name})",
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
}