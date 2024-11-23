package com.pavellukyanov.themartian.ui.screens.home

import android.webkit.WebView
import android.webkit.WebViewClient
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.pavellukyanov.themartian.R
import com.pavellukyanov.themartian.domain.entity.Rover
import com.pavellukyanov.themartian.ui.theme.MediaRed
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
        reducer.dispatch(HomeAction.LoadRovers)
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
                onClick = reducer::dispatch
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
    var privacyPolicyState by remember { mutableStateOf(false) }

    if (privacyPolicyState) PrivacyPolicyWebView(onBackClick = { privacyPolicyState = !privacyPolicyState })
    else LazyColumn(
        state = rememberLazyListState(),
        modifier = modifier.padding(top = 32.dp)
    ) {
        //App name
        item {
            Text(
                modifier = Modifier
                    .padding(vertical = 54.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = stringResource(id = R.string.app_name),
                color = Color.White,
                fontWeight = FontWeight.Medium,
                fontSize = 28.sp
            )
        }

        //Rovers
        state.rovers.forEach { rover ->
            item {
                RoverContent(rover = rover, onClick = { onClick(HomeAction.OnRoverClick(rover = it)) })
            }
        }

        item {
            Text(
                text = stringResource(id = R.string.privacy_policy),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable { privacyPolicyState = !privacyPolicyState },
                color = Color.Black.copy(alpha = 0.8f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
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
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Button(
                modifier = Modifier
                    .size(40.dp),
                onClick = onBackClick,
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                Icon(
                    tint = Color.Black,
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.button_back)
                )
            }
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = "Privacy Policy",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
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

private const val STATUS = "active"

@Composable
private fun RoverContent(
    rover: Rover,
    onClick: (Rover) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .fillMaxWidth()
            .background(color = Color.LightGray.copy(alpha = 0.3f), shape = RoundedCornerShape(16.dp))
            .clickable { onClick(rover) }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            //Header
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                //Rover Name
                Text(
                    modifier = Modifier
                        .weight(3f),
                    text = rover.roverName,
                    color = Color.White,
                    fontSize = 18.sp,
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Start
                )
                //Status
                Text(
                    text = rover.status,
                    color = if (rover.status == STATUS) Color.Green else MediaRed,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.End
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            //Dates
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier
                        .weight(3f),
                    text = stringResource(R.string.launch_date),
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Start
                )
                Text(
                    text = rover.launchDate,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    fontSize = 12.sp,
                    textAlign = TextAlign.End
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier
                        .weight(3f),
                    text = stringResource(R.string.landing_date),
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Start
                )
                Text(
                    text = rover.landingDateFormat,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    fontSize = 12.sp,
                    textAlign = TextAlign.End
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            //Photos
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier
                        .weight(3f),
                    text = stringResource(R.string.last_photo_date),
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Start
                )
                Text(
                    text = rover.maxDateFormat,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    fontSize = 12.sp,
                    textAlign = TextAlign.End
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier
                        .weight(3f),
                    text = stringResource(R.string.total_photo),
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Start
                )
                Text(
                    text = rover.totalPhotos.toString(),
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    fontSize = 12.sp,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}