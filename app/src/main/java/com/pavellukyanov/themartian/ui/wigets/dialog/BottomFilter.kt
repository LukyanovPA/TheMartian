package com.pavellukyanov.themartian.ui.wigets.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pavellukyanov.themartian.R
import com.pavellukyanov.themartian.utils.DateFormatter
import com.pavellukyanov.themartian.utils.ext.Launch
import com.pavellukyanov.themartian.utils.ext.suspendDebugLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomFilter(
    currentDate: String,
    isFavourites: Boolean,
    paddingValues: PaddingValues,
    onShowBottomSheetState: (Boolean) -> Unit,
    onNewDate: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var showDatePicker by remember { mutableStateOf(false) }
    var currentDateTriple by remember { mutableStateOf(Triple(0, 0, 0)) }

    Launch {
        launch(Dispatchers.Default) {
            currentDateTriple = DateFormatter.parse(currentDate)
            suspendDebugLog { "date -> $currentDateTriple" }
        }
    }

    if (showDatePicker) DateDialog(
        startYear = currentDateTriple.first,
        startMonth = currentDateTriple.second,
        startDay = currentDateTriple.third,
        onShowDatePicker = { showDatePicker = it },
        onNewDate = onNewDate
    )

    ModalBottomSheet(
        modifier = Modifier
            .padding(paddingValues),
        containerColor = Color.Transparent,
        onDismissRequest = { onShowBottomSheetState(false) },
        sheetState = sheetState
    ) {
        LazyColumn(
            state = rememberLazyListState(),
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(Color.White)
                .fillMaxSize()
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
                        color = Color.DarkGray,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            //Date
            item {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = stringResource(id = R.string.filter_current_date),
                        color = Color.Black,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.Start
                    )
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = currentDate,
                        color = Color.Black,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.Start
                    )
                    Button(
                        onClick = {
                            showDatePicker = true
                        }
                    ) {
                        Text(
                            text = "Change",
                            color = Color.Black,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp,
                            textAlign = TextAlign.Start
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateDialog(
    startYear: Int,
    startMonth: Int,
    startDay: Int,
    onShowDatePicker: (Boolean) -> Unit,
    onNewDate: (String) -> Unit
) {
    val calendar = Calendar.getInstance()
    calendar.set(startYear, startMonth, startDay)

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = calendar.timeInMillis)
    var selectedDate by remember { mutableLongStateOf(calendar.timeInMillis) }
    val scope = rememberCoroutineScope()

    DatePickerDialog(
        onDismissRequest = {
            onShowDatePicker(false)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    scope.launch {
                        onShowDatePicker(false)
                        selectedDate = datePickerState.selectedDateMillis!!
                        onNewDate(SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(Date(selectedDate)))
                    }
                }
            ) {
                Text(text = stringResource(id = R.string.any_screen_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onShowDatePicker(false)
            }) {
                Text(text = stringResource(id = R.string.any_screen_chancel))
            }
        }
    ) {
        DatePicker(
            state = datePickerState
        )
    }
}