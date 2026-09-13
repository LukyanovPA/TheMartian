package com.pavellukyanov.themartian.ui.wigets.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pavellukyanov.themartian.R
import com.pavellukyanov.themartian.domain.entity.CacheItem
import com.pavellukyanov.themartian.ui.theme.AccentMars
import com.pavellukyanov.themartian.ui.theme.BgDeep
import com.pavellukyanov.themartian.ui.theme.MartianType
import com.pavellukyanov.themartian.ui.theme.SurfaceBorder
import com.pavellukyanov.themartian.ui.theme.SurfaceMuted
import com.pavellukyanov.themartian.ui.theme.TextPrimary
import com.pavellukyanov.themartian.ui.theme.TextTertiary
import com.pavellukyanov.themartian.ui.wigets.chart.CircularChart
import com.pavellukyanov.themartian.ui.wigets.dialog.ChooseDialog

@Composable
fun SettingsDrawer(
    items: List<CacheItem>,
    currentCacheSize: Float,
    paddingValues: PaddingValues,
    onDeleteCache: () -> Unit,
    onCacheSizeChange: (Float) -> Unit,
    onFavouritesClick: () -> Unit
) {
    var showChooseDialog by remember { mutableStateOf(false) }
    var sliderPosition by remember { mutableFloatStateOf(0F) }
    sliderPosition = currentCacheSize

    if (showChooseDialog) ChooseDialog(
        text = stringResource(R.string.delete_cache_dialog),
        onSuccess = {
            onDeleteCache()
            showChooseDialog = false
        },
        onClose = { showChooseDialog = false }
    )

    ModalDrawerSheet(drawerContainerColor = BgDeep) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BgDeep),
            contentAlignment = Alignment.BottomCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //Title
                item {
                    Text(
                        modifier = Modifier
                            .padding(bottom = 28.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = stringResource(id = R.string.settings_title),
                        style = MartianType.ScreenTitle,
                        color = TextPrimary
                    )
                }

                //Cache Chart
                item {
                    CircularChart(items = items, currentSize = currentCacheSize)
                }

                //Delete Cache Button
                item {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 18.dp)
                            .background(color = SurfaceMuted, shape = RoundedCornerShape(14.dp))
                            .clickable { showChooseDialog = true }
                            .padding(vertical = 14.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.delete_cache_button_title),
                            style = MartianType.Body,
                            color = TextPrimary
                        )
                    }
                }

                //Change Cache Size
                item {
                    Column(modifier = Modifier.padding(top = 24.dp)) {
                        Text(
                            modifier = Modifier.padding(bottom = 12.dp),
                            text = stringResource(id = R.string.cache_change_description),
                            style = MartianType.BodySmall,
                            color = TextTertiary
                        )
                        Slider(
                            value = sliderPosition,
                            onValueChange = {
                                sliderPosition = it
                                onCacheSizeChange(it)
                            },
                            colors = SliderDefaults.colors(
                                thumbColor = AccentMars,
                                activeTrackColor = AccentMars,
                                inactiveTrackColor = SurfaceBorder
                            ),
                            valueRange = 0f..100f
                        )
                        Text(
                            text = stringResource(id = R.string.cache_size_mb, sliderPosition.toInt()),
                            style = MartianType.MonoValue,
                            color = AccentMars
                        )
                    }
                }

                //Favourites
                item {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 14.dp)
                            .background(color = SurfaceMuted, shape = RoundedCornerShape(14.dp))
                            .clickable(onClick = onFavouritesClick)
                            .padding(vertical = 14.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.favourites_title),
                            style = MartianType.Body,
                            color = TextPrimary
                        )
                        Icon(
                            modifier = Modifier.padding(start = 6.dp),
                            tint = AccentMars,
                            imageVector = Icons.AutoMirrored.Filled.ArrowRightAlt,
                            contentDescription = stringResource(id = R.string.favourites_title)
                        )
                    }
                }
            }
        }
    }
}
