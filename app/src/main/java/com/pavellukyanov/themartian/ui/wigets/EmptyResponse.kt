package com.pavellukyanov.themartian.ui.wigets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pavellukyanov.themartian.R
import com.pavellukyanov.themartian.ui.theme.AccentMars
import com.pavellukyanov.themartian.ui.theme.GolosFontFamily
import com.pavellukyanov.themartian.ui.theme.TextPrimary
import com.pavellukyanov.themartian.ui.theme.TextSecondary

@Composable
fun EmptyResponse(
    modifier: Modifier,
    isFavourites: Boolean
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = Modifier
                    .size(45.dp),
                imageVector = Icons.Outlined.SearchOff,
                contentDescription = stringResource(id = R.string.empty_state_icon_description),
                tint = AccentMars
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                textAlign = TextAlign.Center,
                text = if (isFavourites) stringResource(id = R.string.empty_response_title_favourites) else stringResource(id = R.string.empty_response_title),
                color = TextPrimary,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                fontFamily = GolosFontFamily
            )
            if (!isFavourites) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    textAlign = TextAlign.Center,
                    text = stringResource(id = R.string.empty_response_body),
                    color = TextSecondary,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    fontFamily = GolosFontFamily
                )
            }
        }
    }
}
