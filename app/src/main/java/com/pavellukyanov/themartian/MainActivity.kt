package com.pavellukyanov.themartian

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pavellukyanov.themartian.ui.NavigationGraph
import com.pavellukyanov.themartian.ui.theme.TheMartianTheme
import com.pavellukyanov.themartian.ui.wigets.SettingsButton
import com.pavellukyanov.themartian.ui.wigets.dialog.ErrorDialog
import com.pavellukyanov.themartian.ui.wigets.drawer.SettingsDrawer
import com.pavellukyanov.themartian.utils.C.EMPTY_STRING
import com.pavellukyanov.themartian.utils.C.ERROR
import com.pavellukyanov.themartian.utils.C.ERROR_BROADCAST_ACTION
import com.pavellukyanov.themartian.utils.ext.Launch
import com.pavellukyanov.themartian.utils.ext.asState
import com.pavellukyanov.themartian.utils.ext.localBroadcast
import com.pavellukyanov.themartian.utils.ext.log
import com.pavellukyanov.themartian.utils.ext.receive
import com.pavellukyanov.themartian.utils.ext.subscribeEffect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject


class MainActivity : ComponentActivity() {
    private val errorReceiver by lazy { initErrorBroadcastReceiver() }
    private val reducer by inject<MainActivityReducer>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registrationErrorBroadcastReceivers()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        setContent {
            TheMartianTheme {
                val state by reducer.asState()
                val navController = rememberNavController()
                val hasError = remember { mutableStateOf(false) }
                val error = remember { mutableStateOf(EMPTY_STRING) }
                val configuration = LocalConfiguration.current
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val isHomeScreen = navBackStackEntry?.destination?.route == "ui/screens/home"

                Launch {
                    if (savedInstanceState == null) reducer.sendAction(MainAction.OnUpdateRoverInfoCache)
                    reducer.subscribeEffect { effect ->
                        when (effect) {
                            is MainEffect.ShowError -> {
                                hasError.value = true
                                error.value = effect.errorMessage
                            }

                            is MainEffect.CloseErrorDialog -> {
                                hasError.value = false
                                error.value = EMPTY_STRING
                            }

                            is MainEffect.OpenFavourites -> navController.navigate("ui/screens/gallery/${getString(R.string.favourites_title)}/${true}")
                        }
                    }
                }

                state.receive<MainState> { currentState ->
                    Scaffold(
                        floatingActionButton = {
                            SettingsButton(
                                isVisible = !drawerState.isOpen && isHomeScreen,
                                onClick = {
                                    scope.launch {
                                        if (drawerState.isOpen) drawerState.close() else drawerState.open()
                                    }
                                }
                            )
                        }
                    ) { padding ->
                        ModalNavigationDrawer(
                            modifier = Modifier
                                .padding(padding),
                            drawerState = drawerState.also {
                                if (it.isOpen) reducer.sendAction(MainAction.OnUpdateSettings)
                            },
                            gesturesEnabled = true,
                            drawerContent = {
                                SettingsDrawer(
                                    items = currentState.cacheItems,
                                    paddingValues = padding,
                                    currentCacheSize = currentState.currentCacheSize,
                                    onDeleteCache = { reducer.sendAction(MainAction.OnDeleteCache) },
                                    onCacheSizeChange = { reducer.sendAction(MainAction.OnCacheSizeChange(size = it)) },
                                    onFavouritesClick = {
                                        reducer.sendAction(MainAction.OnFavouritesClick)
                                        scope.launch {
                                            drawerState.close()
                                        }
                                    }
                                )
                            }
                        ) {
                            NavigationGraph(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .paint(
                                        painterResource(id = R.drawable.main_background),
                                        contentScale = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) ContentScale.Crop else ContentScale.FillHeight
                                    ),
                                navController = navController
                            )

                            if (hasError.value) ErrorDialog(
                                errorText = error.value,
                                padding = padding,
                                onClose = { reducer.sendAction(MainAction.CloseErrorDialog) }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun registrationErrorBroadcastReceivers() {
        localBroadcast().registerReceiver(errorReceiver, IntentFilter(ERROR_BROADCAST_ACTION))
        log.w("registrationErrorBroadcastReceivers")
    }

    private fun initErrorBroadcastReceiver(): BroadcastReceiver =
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                (intent?.getSerializableExtra(ERROR) as? Throwable)?.let { error ->
                    reducer.sendAction(MainAction.Error(error = error))
                    log.w("onReceiveError -> ${error.javaClass.simpleName}")
                }
            }
        }

    override fun onDestroy() {
        super.onDestroy()
        localBroadcast().unregisterReceiver(errorReceiver)
    }
}