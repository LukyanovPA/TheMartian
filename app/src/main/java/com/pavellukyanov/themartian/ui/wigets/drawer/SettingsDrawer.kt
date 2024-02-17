package com.pavellukyanov.themartian.ui.wigets.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pavellukyanov.themartian.BuildConfig
import com.pavellukyanov.themartian.R
import com.pavellukyanov.themartian.domain.entity.CacheItem
import com.pavellukyanov.themartian.ui.theme.DbPink
import com.pavellukyanov.themartian.ui.theme.GrayBac
import com.pavellukyanov.themartian.ui.theme.MediaRed
import com.pavellukyanov.themartian.ui.wigets.chart.CircularChart

@Composable
fun SettingsDrawer(
    items: List<CacheItem>,
    currentCacheSize: Float,
    paddingValues: PaddingValues,
    onDeleteCache: () -> Unit,
    onCacheSizeChange: (Float) -> Unit
) {
    var sliderPosition by remember { mutableFloatStateOf(0F) }
    sliderPosition = currentCacheSize

    ModalDrawerSheet {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(GrayBac),
            contentAlignment = Alignment.BottomCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //Title
                item {
                    Text(
                        modifier = Modifier
                            .padding(bottom = 32.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = stringResource(id = R.string.settings_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    )
                }

                //Cache Chart
                item {
                    CircularChart(items = items)
                }

                //Delete Cache Button
                item {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        onClick = {
                            onDeleteCache()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue, contentColor = Color.White)
                    ) {
                        Text(text = stringResource(id = R.string.delete_cache_button_title))
                    }
                }

                //Change Cache Size
                item {
                    Column {
                        Text(
                            modifier = Modifier
                                .padding(vertical = 16.dp),
                            text = stringResource(id = R.string.cache_change_description)
                        )
                        Slider(
                            value = sliderPosition,
                            onValueChange = {
                                sliderPosition = it
                                onCacheSizeChange(it)
                            },
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.secondary,
                                activeTrackColor = MediaRed,
                                inactiveTrackColor = DbPink
                            ),
                            valueRange = 0f..100f
                        )
                        Text(
                            text = stringResource(id = R.string.cache_size_mb, sliderPosition.toInt()),
                            fontWeight = FontWeight.Medium,
                            color = Color.Blue
                        )
                    }
                }
            }

            //Version
            Text(
                modifier = Modifier
                    .padding(bottom = 36.dp),
                text = "version ${BuildConfig.VERSION_NAME}"
            )
        }
    }
}