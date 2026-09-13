package com.pavellukyanov.themartian.ui.wigets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pavellukyanov.themartian.R
import com.pavellukyanov.themartian.ui.theme.SurfaceBorder
import com.pavellukyanov.themartian.ui.theme.SurfaceCard
import com.pavellukyanov.themartian.ui.theme.TextSecondary

@Composable
fun SettingsButton(
    isVisible: Boolean,
    onClick: () -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Button(
            modifier = Modifier
                .padding(bottom = 92.dp, end = 4.dp)
                .size(44.dp)
                .background(color = SurfaceCard, shape = CircleShape)
                .border(width = 1.dp, color = SurfaceBorder, shape = CircleShape),
            onClick = onClick,
            shape = CircleShape,
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color.Transparent)
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                tint = TextSecondary,
                imageVector = Icons.Filled.Settings,
                contentDescription = stringResource(id = R.string.settings_button_description)
            )
        }
    }
}
