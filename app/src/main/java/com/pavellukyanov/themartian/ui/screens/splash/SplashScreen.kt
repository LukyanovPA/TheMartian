package com.pavellukyanov.themartian.ui.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.pavellukyanov.themartian.R
import com.pavellukyanov.themartian.ui.screens.gallery.GalleryState
import com.pavellukyanov.themartian.utils.ext.Launch
import com.pavellukyanov.themartian.utils.ext.asState
import com.pavellukyanov.themartian.utils.ext.receive
import com.pavellukyanov.themartian.utils.ext.subscribeEffect
import org.koin.androidx.compose.koinViewModel

@Composable
fun SplashScreen(
    modifier: Modifier,
    navController: NavHostController,
    reducer: SplashReducer = koinViewModel()
) {
    val state by reducer.asState()

    Launch {
        reducer.dispatch(SplashAction)
        reducer.subscribeEffect { effect ->
            if (effect.state) navController.navigate("ui/screens/home")
        }
    }

    state.receive<GalleryState>(
        content = { _ ->
            Box(
                modifier = modifier
                    .background(Color.DarkGray)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier.size(110.dp),
                    painter = painterResource(id = R.drawable.ic_mars),
                    contentDescription = stringResource(id = R.string.default_image_description)
                )
            }
        }
    )
}