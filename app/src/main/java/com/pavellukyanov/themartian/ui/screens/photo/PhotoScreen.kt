package com.pavellukyanov.themartian.ui.screens.photo

import android.app.DownloadManager
import android.content.Context
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.pavellukyanov.themartian.R
import com.pavellukyanov.themartian.data.dto.Photo
import com.pavellukyanov.themartian.ui.theme.AccentMars
import com.pavellukyanov.themartian.ui.theme.BgDeep
import com.pavellukyanov.themartian.ui.theme.MartianType
import com.pavellukyanov.themartian.ui.theme.SurfaceBorder
import com.pavellukyanov.themartian.ui.theme.SurfaceCard
import com.pavellukyanov.themartian.ui.theme.TextPrimary
import com.pavellukyanov.themartian.ui.theme.TextTertiary
import com.pavellukyanov.themartian.ui.wigets.dialog.ChooseDialog
import com.pavellukyanov.themartian.ui.wigets.img.Picture
import com.pavellukyanov.themartian.utils.BrowseContext
import com.pavellukyanov.themartian.utils.ext.Launch
import com.pavellukyanov.themartian.utils.ext.asState
import com.pavellukyanov.themartian.utils.ext.receive
import com.pavellukyanov.themartian.utils.ext.subscribeEffect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

/** The image can be zoomed in for detail but never past a point where it loses the frame entirely. */
private const val MIN_SCALE = 1f
private const val MAX_SCALE = 4f

@Composable
fun PhotoScreen(
    photoId: Int,
    modifier: Modifier,
    navController: NavHostController,
    reducer: PhotoReducer = koinViewModel()
) {
    val state by reducer.asState()
    val context = LocalContext.current

    Launch {
        reducer.dispatch(PhotoAction.LoadPhoto(photoId = photoId))
        reducer.subscribeEffect { effect ->
            when (effect) {
                is PhotoEffect.OnBackClick -> navController.popBackStack()
                is PhotoEffect.OnDownload -> launch(Dispatchers.IO) {
                    (context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager).enqueue(effect.request)
                }
                // Pop the current photo before pushing the next one, so paging ←/→ never grows
                // the back stack — back always lands on the gallery, no matter how many photos
                // were paged through, the same way it would if only one had ever been opened.
                is PhotoEffect.NavigateToPhoto -> {
                    navController.popBackStack()
                    navController.navigate("ui/screens/photo/${effect.photoId}")
                }
            }
        }
    }

    state.receive<PhotoState>(
        content = { currentState ->
            PhotoScreenContent(
                modifier = modifier,
                isFavourites = currentState.isFavourites,
                photo = currentState.photo,
                browseContext = currentState.browseContext,
                onBackClick = { reducer.dispatch(PhotoAction.OnBackClick) },
                onDownloadClick = { reducer.dispatch(PhotoAction.DownloadPhoto(photo = currentState.photo)) },
                onChangeFavouritesClick = { reducer.dispatch(PhotoAction.ChangeFavourites(photo = currentState.photo)) },
                onPreviousClick = { reducer.dispatch(PhotoAction.OnPreviousClick) },
                onNextClick = { reducer.dispatch(PhotoAction.OnNextClick) },
                onError = { reducer.dispatch(PhotoAction.OnImageError(error = it)) }
            )
        }
    )
}

@Composable
private fun PhotoScreenContent(
    modifier: Modifier,
    isFavourites: Boolean,
    photo: Photo?,
    browseContext: BrowseContext?,
    onBackClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onChangeFavouritesClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onError: (Throwable) -> Unit
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val transformState = rememberTransformableState { zoomChange, offsetChange, _ ->
        val newScale = (scale * zoomChange).coerceIn(MIN_SCALE, MAX_SCALE)
        // Panning is only meaningful once zoomed in; at rest the image stays centered.
        offset = if (newScale > MIN_SCALE) offset + offsetChange else Offset.Zero
        scale = newScale
    }
    var showChooseDialog by remember { mutableStateOf(false) }

    if (showChooseDialog) ChooseDialog(
        text = stringResource(R.string.photo_download_dialog),
        onSuccess = {
            onDownloadClick()
            showChooseDialog = false
        },
        onClose = { showChooseDialog = false }
    )

    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
            .background(BgDeep)
    ) {
        val (photoBox, header, prevButton, nextButton, info) = createRefs()

        //Photo
        Picture(
            url = photo?.src,
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
                    translationX = offset.x,
                    translationY = offset.y
                )
                .transformable(state = transformState),
            onError = onError
        )

        //Header
        Box(
            modifier = Modifier
                .constrainAs(header) {
                    top.linkTo(parent.top, margin = 40.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                }
                .fillMaxWidth()
        ) {
            RoundIconButton(modifier = Modifier.align(Alignment.CenterStart), onClick = onBackClick) {
                Icon(
                    tint = TextPrimary,
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.button_back)
                )
            }

            if (browseContext != null) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "${browseContext.position} / ${browseContext.total}",
                    style = MartianType.MonoTag,
                    color = TextPrimary
                )
            }

            Row(
                modifier = Modifier.align(Alignment.CenterEnd),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                RoundIconButton(onClick = { showChooseDialog = true }) {
                    Icon(
                        tint = TextPrimary,
                        modifier = Modifier.padding(4.dp),
                        painter = painterResource(id = R.drawable.ic_download),
                        contentDescription = stringResource(id = R.string.button_back)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                RoundIconButton(onClick = onChangeFavouritesClick) {
                    Icon(
                        tint = AccentMars,
                        modifier = Modifier.padding(4.dp),
                        painter = if (isFavourites) painterResource(id = R.drawable.ic_favourites) else painterResource(id = R.drawable.ic_is_not_favourites),
                        contentDescription = stringResource(id = R.string.button_back)
                    )
                }
            }
        }

        //Prev/next — only rendered when the browse session actually has a neighbour.
        if (browseContext?.previousId != null) {
            RoundIconButton(
                modifier = Modifier.constrainAs(prevButton) {
                    top.linkTo(photoBox.top)
                    bottom.linkTo(photoBox.bottom)
                    start.linkTo(parent.start, margin = 12.dp)
                },
                onClick = onPreviousClick
            ) {
                Icon(
                    tint = TextPrimary,
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.button_previous_photo)
                )
            }
        }
        if (browseContext?.nextId != null) {
            RoundIconButton(
                modifier = Modifier.constrainAs(nextButton) {
                    top.linkTo(photoBox.top)
                    bottom.linkTo(photoBox.bottom)
                    end.linkTo(parent.end, margin = 12.dp)
                },
                onClick = onNextClick
            ) {
                Icon(
                    tint = TextPrimary,
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = stringResource(id = R.string.button_next_photo)
                )
            }
        }

        //Info panel
        PhotoInfoPanel(
            modifier = Modifier.constrainAs(info) {
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            photo = photo
        )
    }
}

@Composable
private fun RoundIconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Button(
        modifier = modifier
            .size(40.dp)
            .background(color = BgDeep.copy(alpha = 0.55f), shape = CircleShape),
        onClick = onClick,
        shape = CircleShape,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        content = { content() }
    )
}

@Composable
private fun PhotoInfoPanel(
    modifier: Modifier,
    photo: Photo?
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.Transparent, BgDeep, BgDeep)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 48.dp, bottom = 28.dp)
                .background(color = SurfaceCard, shape = RoundedCornerShape(18.dp))
                .padding(16.dp)
        ) {
            Text(
                text = photo?.cameraFullName.takeUnless { it.isNullOrBlank() } ?: photo?.cameraName.orEmpty(),
                style = MartianType.CardTitle,
                color = TextPrimary
            )
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = "${photo?.roverName.orEmpty()} · ${stringResource(id = R.string.sol)} ${photo?.sol?.toString().orEmpty()} · ${photo?.earthFormattedDate.orEmpty()}",
                style = MartianType.MonoCaption,
                color = TextTertiary
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                InfoCell(modifier = Modifier.weight(1f), label = stringResource(id = R.string.sol), value = photo?.sol?.toString().orEmpty())
                InfoCell(modifier = Modifier.weight(1f), label = stringResource(id = R.string.earth_date), value = photo?.earthFormattedDate.orEmpty())
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                InfoCell(modifier = Modifier.weight(1f), label = stringResource(id = R.string.rover_name), value = photo?.roverName.orEmpty())
                InfoCell(modifier = Modifier.weight(1f), label = stringResource(id = R.string.camera_name), value = photo?.cameraName.orEmpty())
            }

            // These only render when the API actually reported them — no field here is invented.
            val badges = buildList {
                photo?.lightingConditions?.takeIf { it.isNotBlank() }?.let { add(stringResource(id = R.string.photo_meta_lighting) to it) }
                if (photo?.isPanoramaPart == true) add(stringResource(id = R.string.photo_meta_panorama) to null)
                if (photo?.hasStereoPair == true) add(stringResource(id = R.string.photo_meta_stereo) to null)
            }
            if (badges.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    badges.forEach { (label, value) ->
                        Text(
                            modifier = Modifier
                                .background(color = SurfaceBorder, shape = RoundedCornerShape(999.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            text = if (value != null) "${label.uppercase()}: $value" else label.uppercase(),
                            style = MartianType.MonoLabel,
                            color = TextPrimary
                        )
                    }
                }
            }

            Text(
                modifier = Modifier.padding(top = 14.dp),
                text = stringResource(id = R.string.photo_credit),
                style = MartianType.MonoCaption,
                color = TextTertiary
            )
        }
    }
}

@Composable
private fun InfoCell(
    modifier: Modifier,
    label: String,
    value: String
) {
    Column(modifier = modifier) {
        Text(text = label.uppercase(), style = MartianType.MonoLabel, color = TextTertiary, textAlign = TextAlign.Start)
        Text(
            modifier = Modifier.padding(top = 3.dp),
            text = value,
            style = MartianType.MonoValue,
            color = TextPrimary,
            textAlign = TextAlign.Start
        )
    }
}
