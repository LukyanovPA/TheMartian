package com.pavellukyanov.themartian.ui.wigets.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pavellukyanov.themartian.R
import com.pavellukyanov.themartian.domain.entity.Camera
import com.pavellukyanov.themartian.domain.entity.PhotosOptions
import com.pavellukyanov.themartian.ui.theme.AccentMars
import com.pavellukyanov.themartian.ui.theme.BgDeep
import com.pavellukyanov.themartian.ui.theme.MartianType
import com.pavellukyanov.themartian.ui.theme.SurfaceBorder
import com.pavellukyanov.themartian.ui.theme.SurfaceCard
import com.pavellukyanov.themartian.ui.theme.SurfaceMuted
import com.pavellukyanov.themartian.ui.theme.TextPrimary
import com.pavellukyanov.themartian.ui.theme.TextTertiary
import com.pavellukyanov.themartian.utils.DateFormatter
import com.pavellukyanov.themartian.utils.ext.Launch
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomFilter(
    rovers: List<String>?,
    chooseRover: String?,
    cameras: List<Camera>,
    options: PhotosOptions,
    isFavourites: Boolean,
    onShowBottomSheetState: (Boolean) -> Unit,
    onNewOptions: (PhotosOptions) -> Unit,
    onChooseRover: (String?) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var showDatePicker by remember { mutableStateOf(false) }
    var showCameraDialog by remember { mutableStateOf(false) }
    var showRoverDialog by remember { mutableStateOf(false) }
    var currentDateTriple by remember { mutableStateOf(Triple(0, 0, 0)) }
    var currentOptions by remember { mutableStateOf(options) }
    var currentRover by remember { mutableStateOf<String?>(null) }
    var currentRovers by remember { mutableStateOf(listOf<String>()) }

    Launch {
        launch {
            currentDateTriple = DateFormatter.parse(options.date)
            currentRover = chooseRover
            currentRovers = rovers ?: listOf()
        }
    }

    if (showDatePicker) DateDialog(
        startYear = currentDateTriple.first,
        startMonth = currentDateTriple.second,
        startDay = currentDateTriple.third,
        onShowDatePicker = { showDatePicker = it },
        onNewDate = {
            currentOptions = currentOptions.copy(date = it, displayDate = DateFormatter.format(it))
        }
    )

    if (showCameraDialog) CameraDialog(
        cameras = cameras,
        onSelect = { currentOptions = currentOptions.copy(camera = it) },
        onClose = { showCameraDialog = false })

    if (showRoverDialog) RoverDialog(
        rovers = currentRovers,
        onSelect = { currentRover = it },
        onClose = { showRoverDialog = false })

    ModalBottomSheet(
        modifier = Modifier.wrapContentHeight(),
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        onDismissRequest = { onShowBottomSheetState(false) },
        sheetState = sheetState
    ) {
        LazyColumn(
            state = rememberLazyListState(),
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp))
                .background(SurfaceCard)
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            //Header
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(
                        modifier = Modifier
                            .padding(top = 10.dp, bottom = 6.dp)
                            .background(color = SurfaceBorder, shape = RoundedCornerShape(999.dp))
                            .height(4.dp)
                            .width(38.dp)
                    )
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 6.dp)
                            .padding(top = 10.dp, bottom = 4.dp),
                        text = stringResource(id = R.string.filter_title),
                        style = MartianType.ScreenTitle,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            //Field(s)
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 8.dp)
                ) {
                    if (isFavourites) {
                        FilterField(
                            modifier = Modifier.fillMaxWidth(),
                            label = stringResource(id = R.string.rover_name),
                            value = currentRover ?: stringResource(id = R.string.filter_camera_dialog_all),
                            onClick = { showRoverDialog = true }
                        )
                    } else {
                        FilterField(
                            modifier = Modifier.weight(1f),
                            label = stringResource(id = R.string.filter_current_date),
                            value = currentOptions.displayDate,
                            onClick = { showDatePicker = true }
                        )
                        FilterField(
                            modifier = Modifier.weight(1f),
                            label = stringResource(id = R.string.camera_name),
                            value = currentOptions.camera ?: stringResource(id = R.string.filter_camera_dialog_all),
                            onClick = { showCameraDialog = true }
                        )
                    }
                }
            }

            //Submit
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 20.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = AccentMars, shape = RoundedCornerShape(14.dp))
                            .clickable {
                                if (isFavourites) onChooseRover(currentRover)
                                else onNewOptions(currentOptions)
                            }
                            .padding(vertical = 15.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.any_screen_confirm),
                            style = MartianType.Body,
                            color = BgDeep
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterField(
    modifier: Modifier,
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .background(color = SurfaceMuted, shape = RoundedCornerShape(12.dp))
            .border(width = 1.dp, color = SurfaceBorder, shape = RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Text(text = label.uppercase(), style = MartianType.MonoLabel, color = TextTertiary)
        Text(
            modifier = Modifier.padding(top = 4.dp),
            text = value,
            style = MartianType.Body,
            color = TextPrimary
        )
    }
}
