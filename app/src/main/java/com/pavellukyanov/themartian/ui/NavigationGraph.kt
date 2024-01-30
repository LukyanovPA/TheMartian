package com.pavellukyanov.themartian.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pavellukyanov.themartian.ui.screens.home.HomeScreen

@Composable
fun NavigationGraph(
    navController: NavHostController,
    paddingValues: PaddingValues
) {
    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        startDestination = "ui/screens/home"
    ) {
        composable(route = "ui/screens/home") {
            HomeScreen(navController = navController, paddingValues = paddingValues)
        }
    }
}