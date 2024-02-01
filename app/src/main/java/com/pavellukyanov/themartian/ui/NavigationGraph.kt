package com.pavellukyanov.themartian.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.pavellukyanov.themartian.ui.screens.gallery.RoverGallery
import com.pavellukyanov.themartian.ui.screens.home.HomeScreen
import com.pavellukyanov.themartian.utils.C.EMPTY_STRING
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
            route = "ui/screens/gallery/{$ROVER_NAME_ARG}",
            arguments = listOf(
                navArgument(name = ROVER_NAME_ARG) {
                    type = NavType.StringType
                    defaultValue = EMPTY_STRING
                }
            )
        ) { backStackEntry ->
            val roverName = backStackEntry.arguments?.getString(ROVER_NAME_ARG, EMPTY_STRING).orEmpty()
            RoverGallery(roverName = roverName, modifier = modifier, navController = navController)
        }
    }
}