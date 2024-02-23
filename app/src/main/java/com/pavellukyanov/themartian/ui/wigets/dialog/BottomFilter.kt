package com.pavellukyanov.themartian.ui.wigets.dialog

import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pavellukyanov.themartian.R
import com.pavellukyanov.themartian.domain.entity.Camera
import com.pavellukyanov.themartian.domain.entity.PhotosOptions
import com.pavellukyanov.themartian.utils.DateFormatter
import com.pavellukyanov.themartian.utils.ext.Launch
import kotlinx.coroutines.Dispatchers
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
        launch(Dispatchers.Default) {
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
        modifier = Modifier
            .wrapContentHeight(),
        containerColor = Color.Transparent,
        onDismissRequest = { onShowBottomSheetState(false) },
        sheetState = sheetState
    ) {
        LazyColumn(
            state = rememberLazyListState(),
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(Color.Transparent.copy(alpha = 0.5f))
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            //Header
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(
                        modifier = Modifier
                            .padding(6.dp)
                            .background(Color.LightGray)
                            .height(2.dp)
                            .width(26.dp)
                    )
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 6.dp)
                            .padding(bottom = 4.dp),
                        text = stringResource(id = R.string.filter_title),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            //Date and camera or Rover
            item {
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    if (isFavourites) {
                        //Rover
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                text = stringResource(id = R.string.rover_name),
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )

                            Button(
                                modifier = Modifier.padding(vertical = 8.dp),
                                onClick = {
                                    showRoverDialog = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                            ) {
                                Text(
                                    text = currentRover ?: stringResource(id = R.string.filter_camera_dialog_all),
                                    color = Color.Black,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        //Date
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                text = stringResource(id = R.string.filter_current_date),
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )

                            Button(
                                modifier = Modifier.padding(vertical = 8.dp),
                                onClick = {
                                    showDatePicker = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                            ) {
                                Text(
                                    text = currentOptions.displayDate,
                                    color = Color.Black,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        //Camera
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                text = stringResource(id = R.string.camera_name),
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )

                            Button(
                                modifier = Modifier.padding(vertical = 8.dp),
                                onClick = {
                                    showCameraDialog = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                            ) {
                                Text(
                                    text = currentOptions.camera ?: stringResource(id = R.string.filter_camera_dialog_all),
                                    color = Color.Black,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            //Button submit
            item {
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(
                        modifier = Modifier.padding(vertical = 8.dp),
                        onClick = {
                            if (isFavourites)
                                onChooseRover(currentRover)
                            else
                                onNewOptions(currentOptions)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Green.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = stringResource(id = R.string.any_screen_confirm),
                            color = Color.White,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}