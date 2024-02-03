package com.pavellukyanov.themartian.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.pavellukyanov.themartian.ui.screens.gallery.GalleryScreen
import com.pavellukyanov.themartian.ui.screens.home.HomeScreen
import com.pavellukyanov.themartian.ui.screens.photo.PhotoScreen
import com.pavellukyanov.themartian.utils.C.EMPTY_STRING
import com.pavellukyanov.themartian.utils.C.IS_LOCAL_ARG
import com.pavellukyanov.themartian.utils.C.ROVER_NAME_ARG

@Composable
fun NavigationGraph(
    modifier: Modifier,
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = "ui/screens/home"
    ) {
        composable(route = "ui/screens/home") {
            HomeScreen(modifier = modifier, navController = navController)
        }
        composable(
            route = "ui/screens/gallery/{$ROVER_NAME_ARG}/{$IS_LOCAL_ARG}",
            arguments = listOf(
                navArgument(name = ROVER_NAME_ARG) {
                    type = NavType.StringType
                    defaultValue = EMPTY_STRING
                },
                navArgument(name = IS_LOCAL_ARG) {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val isLocal = backStackEntry.arguments?.getBoolean(IS_LOCAL_ARG, false) ?: false
            val roverName = backStackEntry.arguments?.getString(ROVER_NAME_ARG, EMPTY_STRING).orEmpty()
            GalleryScreen(roverName = roverName, modifier = modifier, navController = navController, isLocal = isLocal)
        }
        composable(route = "ui/screens/photo") {
            PhotoScreen(modifier = modifier, navController = navController)
        }
    }
}