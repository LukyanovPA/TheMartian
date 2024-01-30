package com.pavellukyanov.themartian

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Scaffold
import androidx.navigation.compose.rememberNavController
import com.pavellukyanov.themartian.ui.NavigationGraph
import com.pavellukyanov.themartian.ui.theme.TheMartianTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TheMartianTheme {
                val navController = rememberNavController()

                Scaffold { padding ->
                    NavigationGraph(
                        navController = navController,
                        paddingValues = padding
                    )
                }
            }
        }
    }
}