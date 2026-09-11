package com.pavellukyanov.themartian.ui.wigets.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pavellukyanov.themartian.R
import com.pavellukyanov.themartian.domain.entity.Camera
import com.pavellukyanov.themartian.ui.theme.GolosFontFamily
import com.pavellukyanov.themartian.ui.theme.SurfaceBorder
import com.pavellukyanov.themartian.ui.theme.SurfaceCard
import com.pavellukyanov.themartian.ui.theme.SurfaceMuted
import com.pavellukyanov.themartian.ui.theme.TextPrimary
import com.pavellukyanov.themartian.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraDialog(
    cameras: List<Camera>,
    onSelect: (String?) -> Unit,
    onClose: () -> Unit
) {
    val showDialog = remember { mutableStateOf(true) }

    BasicAlertDialog(
        onDismissRequest = {
            showDialog.value = false
            onClose()
        }
    ) {
        //Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = SurfaceCard,
                    shape = RoundedCornerShape(size = 16.dp)
                )
                .padding(all = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                color = TextPrimary,
                fontFamily = GolosFontFamily,
                text = stringResource(R.string.filter_camera_dialog_title)
            )

            LazyColumn(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .height(300.dp)
            ) {
                item {
                    CameraContent(
                        camera = null,
                        onClick = {
                            onSelect(it?.name)
                            showDialog.value = false
                            onClose()
                        }
                    )
                    cameras.forEach { camera ->
                        CameraContent(
                            camera = camera,
                            onClick = {
                                onSelect(it?.name)
                                showDialog.value = false
                                onClose()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CameraContent(
    camera: Camera?,
    onClick: (Camera?) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .background(shape = RoundedCornerShape(10.dp), color = SurfaceMuted)
            .border(width = 1.dp, color = SurfaceBorder, shape = RoundedCornerShape(10.dp))
            .clickable { onClick(camera) },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier
                .padding(6.dp),
            text = camera?.name ?: stringResource(id = R.string.filter_camera_dialog_all),
            fontWeight = FontWeight.Medium,
            color = TextPrimary,
            fontFamily = GolosFontFamily,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )

        camera?.cameraFullName?.let {
            Text(
                modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .padding(bottom = 6.dp),
                text = it,
                fontWeight = FontWeight.Normal,
                color = TextSecondary,
                fontFamily = GolosFontFamily,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
