package com.pavellukyanov.themartian

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.pavellukyanov.themartian.services.CacheService
import com.pavellukyanov.themartian.ui.NavigationGraph
import com.pavellukyanov.themartian.ui.theme.TheMartianTheme
import com.pavellukyanov.themartian.ui.wigets.dialog.ErrorDialog
import com.pavellukyanov.themartian.utils.C.CACHE_BROADCAST_ACTION
import com.pavellukyanov.themartian.utils.C.EMPTY_STRING
import com.pavellukyanov.themartian.utils.C.ERROR_BROADCAST_ACTION
import com.pavellukyanov.themartian.utils.C.ERROR_MESSAGE
import com.pavellukyanov.themartian.utils.C.OK_RESULT
import com.pavellukyanov.themartian.utils.ext.Launch
import com.pavellukyanov.themartian.utils.ext.checkSdkVersion
import com.pavellukyanov.themartian.utils.ext.log
import com.pavellukyanov.themartian.utils.ext.subscribeEffect
import com.pavellukyanov.themartian.utils.ext.suspendDebugLog
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val cacheReceiver by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { initCacheBroadcastReceiver() }
    private val errorReceiver by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { initErrorBroadcastReceiver() }
    private val reducer by inject<MainActivityReducer>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        registrationBroadcastReceivers()
        updateCache()

        setContent {
            TheMartianTheme {
                val navController = rememberNavController()
                val hasError = remember { mutableStateOf(false) }
                val error = remember { mutableStateOf(EMPTY_STRING) }
                val configuration = LocalConfiguration.current

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
                        }
                    }
                }

                Scaffold { padding ->
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

    private fun registrationBroadcastReceivers() {
        checkSdkVersion(
            less33 = {
                registerReceiver(errorReceiver, IntentFilter(ERROR_BROADCAST_ACTION))
                registerReceiver(cacheReceiver, IntentFilter(CACHE_BROADCAST_ACTION))
                log.w("registrationBroadcastReceivers API < 33")
            },
            more33 = {
                registerReceiver(errorReceiver, IntentFilter(ERROR_BROADCAST_ACTION), RECEIVER_NOT_EXPORTED)
                registerReceiver(cacheReceiver, IntentFilter(CACHE_BROADCAST_ACTION), RECEIVER_NOT_EXPORTED)
                log.w("registrationBroadcastReceivers API > 33")
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
                intent?.getBooleanExtra(OK_RESULT, false)?.let { state ->
                    lifecycleScope.launch {
                        suspendDebugLog { "onReceive state -> $state" }
                        if (state) {
                            stopCacheService()
                            unregisterCacheBroadcastReceiver()
                        }
                    }
                }
            }
        }

    private fun initErrorBroadcastReceiver(): BroadcastReceiver =
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                log.w("initErrorBroadcastReceiver -> $intent")
                intent?.getStringExtra(ERROR_MESSAGE)?.let { errorMessage ->
                    reducer.sendAction(MainAction.Error(errorMessage = errorMessage))
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

    private fun unregisterBroadcastReceivers() {
        log.w("unregisterBroadcastReceivers")
        unregisterReceiver(errorReceiver)
    }

    override fun onDestroy() {
        unregisterBroadcastReceivers()
        super.onDestroy()
    }
}