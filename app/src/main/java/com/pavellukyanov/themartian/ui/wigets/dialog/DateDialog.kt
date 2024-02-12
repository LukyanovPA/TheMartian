package com.pavellukyanov.themartian.ui.wigets.dialog

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.pavellukyanov.themartian.R
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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
    calendar.set(startYear, startMonth - 1, startDay)

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