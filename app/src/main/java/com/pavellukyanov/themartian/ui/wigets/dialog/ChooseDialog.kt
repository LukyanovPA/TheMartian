package com.pavellukyanov.themartian.ui.wigets.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pavellukyanov.themartian.R
import com.pavellukyanov.themartian.ui.theme.AccentMars
import com.pavellukyanov.themartian.ui.theme.BgDeep
import com.pavellukyanov.themartian.ui.theme.GolosFontFamily
import com.pavellukyanov.themartian.ui.theme.SurfaceBorder
import com.pavellukyanov.themartian.ui.theme.SurfaceCard
import com.pavellukyanov.themartian.ui.theme.SurfaceMuted
import com.pavellukyanov.themartian.ui.theme.TextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseDialog(
    text: String,
    onSuccess: () -> Unit,
    onClose: () -> Unit,
) {
    var showDialog by remember { mutableStateOf(true) }

    BasicAlertDialog(
        onDismissRequest = {
            showDialog = false
            onClose()
        }
    ) {
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
            Column {
                Text(
                    textAlign = TextAlign.Center,
                    color = TextPrimary,
                    fontFamily = GolosFontFamily,
                    text = text
                )
            }
            Column {
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .background(shape = RoundedCornerShape(10.dp), color = AccentMars)
                            .clickable { onSuccess() },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(6.dp),
                            text = stringResource(id = R.string.any_screen_confirm),
                            fontWeight = FontWeight.SemiBold,
                            color = BgDeep,
                            fontFamily = GolosFontFamily,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .background(shape = RoundedCornerShape(10.dp), color = SurfaceMuted)
                            .border(width = 1.dp, color = SurfaceBorder, shape = RoundedCornerShape(10.dp))
                            .clickable { onClose() },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(6.dp),
                            text = stringResource(id = R.string.any_screen_chancel),
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary,
                            fontFamily = GolosFontFamily,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
