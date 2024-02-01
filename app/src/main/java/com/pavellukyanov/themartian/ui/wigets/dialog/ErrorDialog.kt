package com.pavellukyanov.themartian.ui.wigets.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pavellukyanov.themartian.R
import com.pavellukyanov.themartian.ui.theme.RedRibbon
import com.pavellukyanov.themartian.utils.C.EMPTY_STRING

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ErrorDialog(
    errorText: String = EMPTY_STRING,
    padding: PaddingValues = PaddingValues(),
    onClose: () -> Unit
) {
    val showDialog = remember { mutableStateOf(true) }

    if (showDialog.value) {
        AlertDialog(
            modifier = Modifier.padding(padding),
            onDismissRequest = {
                showDialog.value = false
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(size = 16.dp)
                        )
                        .padding(all = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        color = RedRibbon,
                        text = stringResource(R.string.any_screen_error)
                    )
                    Text(
                        modifier = Modifier.padding(vertical = 16.dp),
                        color = Color.DarkGray,
                        text = errorText
                    )
                    Button(
                        modifier = Modifier
                            .padding(8.dp),
                        onClick = {
                            showDialog.value = false
                            onClose()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RedRibbon)
                    ) {
                        Text(
                            color = Color.White,
                            text = stringResource(id = R.string.any_screen_ok),
                            fontSize = 18.sp
                        )
                    }
                }
            }
        )
    }
}