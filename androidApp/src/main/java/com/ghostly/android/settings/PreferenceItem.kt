package com.ghostly.android.settings

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun PreferenceItem(
    icon: ImageVector? = null,
    @StringRes iconDesc: Int? = null,
    name: String,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .clickable { onClick.invoke() }
            .padding(vertical = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            icon?.let {
                Icon(
                    icon,
                    contentDescription = iconDesc?.let { stringResource(id = iconDesc) },
                    modifier = Modifier
                        .size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = name,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(modifier = Modifier.weight(1.0f))
        Icon(
            Icons.AutoMirrored.Rounded.KeyboardArrowRight,
            tint = MaterialTheme.colorScheme.surfaceTint,
            contentDescription = "Next"
        )
    }
}

@Composable
fun PreferenceItem(
    @DrawableRes icon: Int? = null,
    @StringRes iconDesc: Int? = null,
    @StringRes name: Int,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .clickable { onClick.invoke() }
            .padding(vertical = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            icon?.let {
                Icon(
                    painterResource(id = icon),
                    contentDescription = stringResource(id = iconDesc ?: 0),
                    modifier = Modifier
                        .size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = stringResource(id = name),
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(modifier = Modifier.weight(1.0f))
        Icon(
            Icons.AutoMirrored.Rounded.KeyboardArrowRight,
            tint = MaterialTheme.colorScheme.surfaceTint,
            contentDescription = "Next"
        )
    }
}