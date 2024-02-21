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
import com.pavellukyanov.themartian.services.CacheService
import com.pavellukyanov.themartian.services.CacheServiceLifecycle
import com.pavellukyanov.themartian.ui.NavigationGraph
import com.pavellukyanov.themartian.ui.theme.TheMartianTheme
import com.pavellukyanov.themartian.ui.wigets.SettingsButton
import com.pavellukyanov.themartian.ui.wigets.dialog.ErrorDialog
import com.pavellukyanov.themartian.ui.wigets.drawer.SettingsDrawer
import com.pavellukyanov.themartian.utils.C.CACHE_BROADCAST_ACTION
import com.pavellukyanov.themartian.utils.C.CACHE_SERVICE_STATUS
import com.pavellukyanov.themartian.utils.C.EMPTY_STRING
import com.pavellukyanov.themartian.utils.C.ERROR
import com.pavellukyanov.themartian.utils.C.ERROR_BROADCAST_ACTION
import com.pavellukyanov.themartian.utils.ext.Launch
import com.pavellukyanov.themartian.utils.ext.asState
import com.pavellukyanov.themartian.utils.ext.checkSdkVersion
import com.pavellukyanov.themartian.utils.ext.debug
import com.pavellukyanov.themartian.utils.ext.log
import com.pavellukyanov.themartian.utils.ext.receive
import com.pavellukyanov.themartian.utils.ext.subscribeEffect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject


class MainActivity : ComponentActivity() {
    private val cacheReceiver by lazy { initCacheBroadcastReceiver() }
    private val errorReceiver by lazy { initErrorBroadcastReceiver() }
    private val reducer by inject<MainActivityReducer>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registrationErrorBroadcastReceivers()

        if (savedInstanceState == null) {
            registrationCacheBroadcastReceivers()
            updateCache()
        }

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
        checkSdkVersion(
            less33 = {
                registerReceiver(errorReceiver, IntentFilter(ERROR_BROADCAST_ACTION))
                log.w("registrationErrorBroadcastReceivers API < 33")
            },
            more33 = {
                registerReceiver(errorReceiver, IntentFilter(ERROR_BROADCAST_ACTION), RECEIVER_NOT_EXPORTED)
                log.w("registrationErrorBroadcastReceivers API > 33")
            }
        )
    }

    private fun registrationCacheBroadcastReceivers() {
        checkSdkVersion(
            less33 = {
                registerReceiver(cacheReceiver, IntentFilter(CACHE_BROADCAST_ACTION))
                log.w("registrationCacheBroadcastReceivers API < 33")
            },
            more33 = {
                registerReceiver(cacheReceiver, IntentFilter(CACHE_BROADCAST_ACTION), RECEIVER_NOT_EXPORTED)
                log.w("registrationCacheBroadcastReceivers API > 33")
            }
        )
    }

    private fun updateCache() {
        log.w("updateCache")
        startForegroundService(Intent(this@MainActivity, CacheService::class.java))
    }

    private fun initCacheBroadcastReceiver(): BroadcastReceiver =
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                debug { "onRecive" }
                intent?.getStringExtra(CACHE_SERVICE_STATUS)?.let { status ->
                    log.d("onReceiveCache -> $status")
                    val serviceStatus = CacheServiceLifecycle.entries.find { it.name == status } ?: CacheServiceLifecycle.DIDNT_START
                    reducer.sendAction(MainAction.OnCacheServiceStatus(cacheServiceStatus = serviceStatus))
                    if (serviceStatus == CacheServiceLifecycle.FINISH || serviceStatus == CacheServiceLifecycle.FINISH_WITH_ERROR) stopCacheService(); unregisterCacheBroadcastReceiver()
                }

//                (intent?.getSerializableExtra(CACHE_SERVICE_STATUS) as? CacheServiceLifecycle)?.let { status ->
//                    log.d("onReceiveCache -> $status")
//                    reducer.sendAction(MainAction.OnCacheServiceStatus(cacheServiceStatus = status))
//                    if (status == CacheServiceLifecycle.FINISH || status == CacheServiceLifecycle.FINISH_WITH_ERROR) stopCacheService(); unregisterCacheBroadcastReceiver()
//                }
            }
        }

    private fun initErrorBroadcastReceiver(): BroadcastReceiver =
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                (intent?.getSerializableExtra(ERROR) as? Throwable)?.let { error ->
                    log.d("onReceiveError -> ${error.javaClass.simpleName}")
                    reducer.sendAction(MainAction.Error(error = error))
                }
            }
        }

    private fun stopCacheService() {
        log.w("stopCacheService")
        stopService(Intent(this@MainActivity, CacheService::class.java))
    }

    private fun unregisterCacheBroadcastReceiver() {
        log.w("unregisterCacheBroadcastReceiver")
        unregisterReceiver(cacheReceiver)
    }

    override fun onDestroy() {
        unregisterReceiver(errorReceiver)
        reducer.sendAction(MainAction.CheckCacheOverSize)
        super.onDestroy()
    }
}