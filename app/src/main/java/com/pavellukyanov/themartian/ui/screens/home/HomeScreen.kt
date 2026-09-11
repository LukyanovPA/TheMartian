package com.pavellukyanov.themartian.ui.screens.home

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.pavellukyanov.themartian.R
import com.pavellukyanov.themartian.data.dto.Photo
import com.pavellukyanov.themartian.domain.entity.Rover
import com.pavellukyanov.themartian.ui.theme.AccentMars
import com.pavellukyanov.themartian.ui.theme.BgDeep
import com.pavellukyanov.themartian.ui.theme.MartianType
import com.pavellukyanov.themartian.ui.theme.StatusActive
import com.pavellukyanov.themartian.ui.theme.StatusMuted
import com.pavellukyanov.themartian.ui.theme.SurfaceBorder
import com.pavellukyanov.themartian.ui.theme.SurfaceCard
import com.pavellukyanov.themartian.ui.theme.SurfaceMuted
import com.pavellukyanov.themartian.ui.theme.TextPrimary
import com.pavellukyanov.themartian.ui.theme.TextSecondary
import com.pavellukyanov.themartian.ui.theme.TextTertiary
import com.pavellukyanov.themartian.ui.wigets.dialog.DisabledRoverDialog
import com.pavellukyanov.themartian.ui.wigets.img.Picture
import com.pavellukyanov.themartian.ui.wigets.nav.BottomNavTab
import com.pavellukyanov.themartian.ui.wigets.nav.MartianBottomNav
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
    var showRoverDataDisabledDialog by remember { mutableStateOf(false) }

    Launch {
        reducer.dispatch(HomeAction.LoadRovers)
        reducer.subscribeEffect { effect ->
            when (effect) {
                is HomeEffect.NavigateToRoverGallery -> navController.navigate("ui/screens/gallery/${effect.roverName}/${false}")
                is HomeEffect.ShowDisabledRoverDialog -> showRoverDataDisabledDialog = true
            }
        }
    }

    if (showRoverDataDisabledDialog) DisabledRoverDialog { showRoverDataDisabledDialog = false }

    state.receive<HomeState>(
        modifier = modifier,
        content = { currentState ->
            HomeScreenContent(
                state = currentState,
                modifier = modifier,
                navController = navController,
                onClick = reducer::dispatch
            )
        }
    )
}

@Composable
private fun HomeScreenContent(
    modifier: Modifier,
    state: HomeState,
    navController: NavHostController,
    onClick: (HomeAction) -> Unit
) {
    var privacyPolicyState by remember { mutableStateOf(false) }
    val favouritesRoute = "ui/screens/gallery/${stringResource(id = R.string.favourites_title)}/${true}"

    // The hero reuses the same best-effort thumbnails the cards below already fetch — no
    // dedicated network call of its own. Whichever active rover has the most recent frame wins;
    // falls back to the first rover so there is still something once none are active.
    val heroRover = state.rovers.filter { it.status == STATUS_ACTIVE }.maxByOrNull { it.maxDate } ?: state.rovers.firstOrNull()
    val heroPhoto = heroRover?.let { state.thumbnails[it.roverName]?.firstOrNull() }

    if (privacyPolicyState) {
        PrivacyPolicyWebView(onBackClick = { privacyPolicyState = !privacyPolicyState })
    } else {
        Box(modifier = modifier.fillMaxSize()) {
            LazyColumn(
                state = rememberLazyListState(),
                modifier = Modifier.padding(top = 20.dp),
                contentPadding = PaddingValues(bottom = 88.dp)
            ) {
                //Wordmark
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(color = AccentMars, shape = CircleShape)
                        )
                        Text(
                            modifier = Modifier.padding(start = 9.dp),
                            text = stringResource(id = R.string.app_name),
                            style = MartianType.ScreenTitle,
                            color = TextPrimary
                        )
                    }
                }

                //Latest frame
                if (heroRover != null) {
                    item {
                        HeroSection(rover = heroRover, photo = heroPhoto)
                    }
                }

                //Section label
                item {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                        text = stringResource(id = R.string.home_section_rovers).uppercase(),
                        style = MartianType.Kicker,
                        color = TextSecondary
                    )
                }

                //Rovers
                state.rovers.forEach { rover ->
                    item {
                        RoverCard(
                            modifier = Modifier.animateItem(),
                            rover = rover,
                            thumbnails = state.thumbnails[rover.roverName].orEmpty(),
                            onClick = { onClick(HomeAction.OnRoverClick(rover = it)) },
                            onLoadThumbnail = { onClick(HomeAction.LoadThumbnail(roverName = rover.roverName)) }
                        )
                    }
                }

                item {
                    Text(
                        text = stringResource(id = R.string.privacy_policy),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp, bottom = 8.dp)
                            .clickable { privacyPolicyState = !privacyPolicyState }
                            .padding(16.dp),
                        style = MartianType.MonoCaption,
                        color = TextTertiary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            MartianBottomNav(
                modifier = Modifier.align(Alignment.BottomCenter),
                selected = BottomNavTab.ROVERS,
                onRoversClick = {},
                onFavouritesClick = {
                    navController.navigate(favouritesRoute) { launchSingleTop = true }
                }
            )
        }
    }
}

@Composable
private fun PrivacyPolicyWebView(
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeep)
            .padding(top = 32.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Button(
                modifier = Modifier.size(40.dp),
                onClick = onBackClick,
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
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = stringResource(id = R.string.privacy_policy),
                style = MartianType.CardTitle,
                color = TextPrimary
            )
        }

        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    webViewClient = WebViewClient()

                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true
                    settings.setSupportZoom(true)
                }
            },
            update = { webView ->
                webView.loadUrl("https://www.freeprivacypolicy.com/live/213d427d-8cba-4986-8ca6-41d60dd6b758")
            }
        )
    }
}

private const val STATUS_ACTIVE = "active"

private const val PREVIEW_TILES = 3

private val HERO_HEIGHT = 196.dp

@Composable
private fun HeroSection(
    rover: Rover,
    photo: Photo?
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(HERO_HEIGHT)
            .background(SurfaceMuted)
    ) {
        if (photo != null) {
            Picture(
                url = photo.src,
                contentDescription = photo.cameraFullName,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                onError = {}
            )
            // Compose gradients run top(0f)-to-bottom(1f) by default — the reverse of the CSS
            // "to top" this mirrors, so the stops are inverted here rather than the axis.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colorStops = arrayOf(
                                0f to BgDeep.copy(alpha = 0.15f),
                                0.58f to BgDeep.copy(alpha = 0.55f),
                                1f to BgDeep
                            )
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.home_hero_kicker).uppercase(),
                    style = MartianType.Kicker,
                    color = AccentMars
                )
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = "${rover.roverName} · ${stringResource(id = R.string.sol)} ${photo.sol}",
                    style = MartianType.ScreenTitle,
                    color = TextPrimary
                )
                Text(
                    modifier = Modifier.padding(top = 2.dp),
                    text = "${photo.cameraName} · ${photo.earthFormattedDate}",
                    style = MartianType.MonoCaption,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun RoverCard(
    modifier: Modifier,
    rover: Rover,
    thumbnails: List<Photo>,
    onClick: (Rover) -> Unit,
    onLoadThumbnail: () -> Unit
) {
    val isActive = rover.status == STATUS_ACTIVE
    val statusColor = if (isActive) StatusActive else StatusMuted

    LaunchedEffect(rover.roverName) { onLoadThumbnail() }

    Column(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .fillMaxWidth()
            .background(color = SurfaceCard, shape = RoundedCornerShape(16.dp))
            .border(width = 1.dp, color = SurfaceBorder, shape = RoundedCornerShape(16.dp))
            .clickable { onClick(rover) }
            .padding(14.dp)
    ) {
        //Header
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = rover.roverName,
                style = MartianType.CardTitle,
                color = TextPrimary
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(color = statusColor.copy(alpha = 0.12f), shape = RoundedCornerShape(999.dp))
                    .padding(horizontal = 9.dp, vertical = 5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(color = statusColor, shape = CircleShape)
                )
                Text(
                    modifier = Modifier.padding(start = 6.dp),
                    text = rover.status.uppercase(),
                    style = MartianType.MonoLabel,
                    color = statusColor
                )
            }
        }

        Spacer(modifier = Modifier.height(13.dp))

        //Stats grid
        Row(modifier = Modifier.fillMaxWidth()) {
            StatCell(modifier = Modifier.weight(1f), label = stringResource(id = R.string.home_stat_launch), value = rover.launchDate)
            StatCell(modifier = Modifier.weight(1f), label = stringResource(id = R.string.home_stat_landing), value = rover.landingDateFormat)
        }
        Spacer(modifier = Modifier.height(11.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            StatCell(modifier = Modifier.weight(1f), label = stringResource(id = R.string.home_stat_last_photo), value = rover.maxDateFormat)
            StatCell(modifier = Modifier.weight(1f), label = stringResource(id = R.string.home_stat_total_photos), value = rover.totalPhotos.toString())
        }

        Spacer(modifier = Modifier.height(13.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            repeat(PREVIEW_TILES) { index ->
                ThumbnailTile(modifier = Modifier.weight(1f), photo = thumbnails.getOrNull(index))
            }
            OverflowTile(modifier = Modifier.weight(1f), count = rover.totalPhotos - PREVIEW_TILES)
        }
    }
}

@Composable
private fun ThumbnailTile(
    modifier: Modifier,
    photo: Photo?
) {
    Box(
        modifier = modifier
            .height(74.dp)
            .background(color = SurfaceMuted, shape = RoundedCornerShape(9.dp))
    ) {
        if (photo != null) {
            Picture(
                url = photo.src,
                contentDescription = photo.cameraFullName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = SurfaceMuted, shape = RoundedCornerShape(9.dp)),
                onError = {}
            )
        }
    }
}

@Composable
private fun OverflowTile(
    modifier: Modifier,
    count: Int
) {
    Box(
        modifier = modifier
            .height(74.dp)
            .background(color = SurfaceMuted, shape = RoundedCornerShape(9.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (count > 0) {
            Text(text = "+$count", style = MartianType.MonoValue, color = TextSecondary)
        }
    }
}

@Composable
private fun StatCell(
    modifier: Modifier,
    label: String,
    value: String
) {
    Column(modifier = modifier) {
        Text(text = label.uppercase(), style = MartianType.MonoLabel, color = TextTertiary)
        Text(
            modifier = Modifier.padding(top = 3.dp),
            text = value,
            style = MartianType.MonoValue,
            color = TextPrimary
        )
    }
}
