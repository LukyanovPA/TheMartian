package com.pavellukyanov.themartian

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST
import androidx.work.WorkManager
import com.pavellukyanov.themartian.ui.NavigationGraph
import com.pavellukyanov.themartian.ui.theme.TheMartianTheme
import com.pavellukyanov.themartian.ui.wigets.SettingsButton
import com.pavellukyanov.themartian.ui.wigets.drawer.SettingsDrawer
import com.pavellukyanov.themartian.utils.ext.Launch
import com.pavellukyanov.themartian.utils.ext.asState
import com.pavellukyanov.themartian.utils.ext.receive
import com.pavellukyanov.themartian.utils.ext.subscribeEffect
import com.pavellukyanov.themartian.utils.work.UpdateRoverInfoCacheWork
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val reducer by inject<MainActivityReducer>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var startDestination = "ui/screens/splash"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            startDestination = "ui/screens/home"
            val splashScreen = installSplashScreen()
            splashScreen.setKeepOnScreenCondition { reducer.isLoading.value }
        }

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        setContent {
            TheMartianTheme {
                val state by reducer.asState()
                val navController = rememberNavController()
                val configuration = LocalConfiguration.current
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val isHomeScreen = navBackStackEntry?.destination?.route == "ui/screens/home"
                val snackbarHostState = remember { SnackbarHostState() }
                val snackbarState = remember { mutableStateOf(SnackbarResult.Dismissed) }
                val context = LocalContext.current

                Launch {
                    if (savedInstanceState == null) reducer.dispatch(MainAction.OnStart)

                    reducer.subscribeEffect { effect ->
                        when (effect) {
                            is MainEffect.ShowError -> {
                                snackbarHostState.currentSnackbarData?.dismiss()

                                launch {
                                    snackbarHostState.showSnackbar(
                                        message = effect.errorMessage,
                                        withDismissAction = true,
                                        duration = SnackbarDuration.Indefinite
                                    ).also { result ->
                                        snackbarState.value = result
                                        if (result == SnackbarResult.Dismissed) reducer.dispatch(MainAction.CloseErrorDialog)
                                    }
                                }
                            }

                            is MainEffect.CloseErrorDialog -> {
                                snackbarHostState.currentSnackbarData?.dismiss()
                            }

                            is MainEffect.OpenFavourites -> navController.navigate("ui/screens/gallery/${getString(R.string.favourites_title)}/${true}")

                            is MainEffect.UpdateRoverInfoCache -> {
                                launch {
                                    val request = OneTimeWorkRequestBuilder<UpdateRoverInfoCacheWork>()
                                        .setExpedited(RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                                        .build()

                                    WorkManager.getInstance(context)
                                        .enqueue(request)
                                }
                            }
                        }
                    }
                }

                state.receive<MainState> { currentState ->
                    Scaffold(
                        snackbarHost = {
                            SnackbarHost(
                                modifier = Modifier
                                    .padding(bottom = 40.dp),
                                hostState = snackbarHostState
                            )
                        },
                        floatingActionButton = {
                            SettingsButton(
                                isVisible = !drawerState.isOpen && isHomeScreen && currentState.settingButtonVisibility,
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
                                if (it.isOpen) reducer.dispatch(MainAction.OnUpdateSettings)
                            },
                            gesturesEnabled = true,
                            drawerContent = {
                                SettingsDrawer(
                                    items = currentState.cacheItems,
                                    paddingValues = padding,
                                    currentCacheSize = currentState.currentCacheSize,
                                    onDeleteCache = { reducer.dispatch(MainAction.OnDeleteCache) },
                                    onCacheSizeChange = { reducer.dispatch(MainAction.OnCacheSizeChange(size = it)) },
                                    onFavouritesClick = {
                                        reducer.dispatch(MainAction.OnFavouritesClick)
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
                                navController = navController,
                                start = startDestination
                            )
                        }
                    }
                }
            }
        }
    }
}