package com.pavellukyanov.themartian.ui.wigets.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pavellukyanov.themartian.R
import com.pavellukyanov.themartian.ui.theme.AccentMars
import com.pavellukyanov.themartian.ui.theme.BgDeep
import com.pavellukyanov.themartian.ui.theme.MartianType
import com.pavellukyanov.themartian.ui.theme.SurfaceBorder
import com.pavellukyanov.themartian.ui.theme.TextTertiary

/** The app's only two top-level destinations. Drill-down screens (a rover's gallery, a single photo) use a back arrow instead. */
enum class BottomNavTab { ROVERS, FAVOURITES }

/**
 * Persistent bottom bar shown on the two top-level screens only (rover roster, favourites).
 * Per-rover gallery and the photo viewer are drill-down destinations reached from Home and are
 * navigated with a back arrow instead — a third "frames" tab has no destination of its own
 * without a rover already chosen, so it is intentionally not part of this bar.
 */
@Composable
fun MartianBottomNav(
    selected: BottomNavTab,
    onRoversClick: () -> Unit,
    onFavouritesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(76.dp)
            .background(BgDeep)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(SurfaceBorder)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            NavItem(
                modifier = Modifier.weight(1f),
                label = stringResource(id = R.string.nav_rovers),
                iconSelected = Icons.Filled.Explore,
                iconUnselected = Icons.Outlined.Explore,
                isSelected = selected == BottomNavTab.ROVERS,
                onClick = onRoversClick
            )
            NavItem(
                modifier = Modifier.weight(1f),
                label = stringResource(id = R.string.favourites_title),
                iconSelected = Icons.Filled.Favorite,
                iconUnselected = Icons.Outlined.FavoriteBorder,
                isSelected = selected == BottomNavTab.FAVOURITES,
                onClick = onFavouritesClick
            )
        }
    }
}

@Composable
private fun NavItem(
    modifier: Modifier,
    label: String,
    iconSelected: ImageVector,
    iconUnselected: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val tint = if (isSelected) AccentMars else TextTertiary

    Column(
        modifier = modifier
            .fillMaxHeight()
            .clickable(onClick = onClick)
            .padding(top = 13.dp, bottom = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = if (isSelected) iconSelected else iconUnselected,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )
        Text(
            modifier = Modifier.padding(top = 6.dp),
            text = label.uppercase(),
            style = MartianType.MonoLabel,
            color = tint
        )
    }
}
