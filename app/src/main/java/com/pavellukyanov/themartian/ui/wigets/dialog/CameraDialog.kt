package com.pavellukyanov.themartian.ui.wigets.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
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
import com.pavellukyanov.themartian.domain.entity.Camera
import com.pavellukyanov.themartian.ui.theme.RedRibbon
import com.pavellukyanov.themartian.utils.C.EMPTY_STRING

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraDialog(
    cameras: List<Camera>,
    modifier: Modifier,
    onSelect: (String) -> Unit,
    onClose: () -> Unit
) {
    val showDialog = remember { mutableStateOf(true) }
    val selected = remember { mutableStateOf(EMPTY_STRING) }

    BasicAlertDialog(
        modifier = modifier,
        onDismissRequest = { showDialog.value = false }
    ) {
        //Header
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
                text = stringResource(R.string.filter_camera_dialog_title)
            )

            //Buttons
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                //Select
                Button(
                    modifier = Modifier
                        .padding(8.dp),
                    enabled = selected.value.isNotEmpty(),
                    onClick = {
                        showDialog.value = false
                        onSelect(selected.value)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedRibbon)
                ) {
                    Text(
                        color = Color.White,
                        text = stringResource(id = R.string.any_screen_confirm),
                        fontSize = 18.sp
                    )
                }

                //Chancel
                Button(
                    modifier = Modifier
                        .padding(8.dp),
                    onClick = {
                        selected.value = EMPTY_STRING
                        showDialog.value = false
                        onClose()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedRibbon)
                ) {
                    Text(
                        color = Color.White,
                        text = stringResource(id = R.string.any_screen_confirm),
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}