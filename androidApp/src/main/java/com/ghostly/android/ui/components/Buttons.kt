package com.ghostly.android.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ghostly.android.theme.AppTheme

@Composable
fun AccentButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = MaterialTheme.shapes.medium,
    colors: ButtonColors = defaultColors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(
        defaultElevation = 2.dp
    ),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit,
) {
    Button(
        onClick,
        modifier,
        enabled,
        shape,
        colors,
        elevation,
        border,
        contentPadding,
        interactionSource,
        content
    )
}

@Composable
fun PrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = MaterialTheme.shapes.medium,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
        contentColor = MaterialTheme.colorScheme.inverseSurface,
        disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
        disabledContentColor = MaterialTheme.colorScheme.inverseSurface
    ),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit,
) {
    Button(
        onClick,
        modifier,
        enabled,
        shape,
        colors,
        elevation,
        border,
        contentPadding,
        interactionSource,
        content
    )
}

@Composable
private fun defaultColors(): ButtonColors = ButtonDefaults.buttonColors(
    containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.9f),
    contentColor = MaterialTheme.colorScheme.onPrimary,
    disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
    disabledContentColor = MaterialTheme.colorScheme.inverseSurface
)

@Composable
fun AccentedExtendedFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = FloatingActionButtonDefaults.extendedFabShape,
    containerColor: Color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.9f),
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(
        defaultElevation = 2.dp
    ),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit,
) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        shape = shape,
        containerColor = containerColor,
        contentColor = contentColor,
        elevation = elevation,
        interactionSource = interactionSource
    ) {
        content()
    }
}


@Preview
@Composable
private fun PreviewButtons() {
    AppTheme {
        PrimaryButton(
            onClick = { },
            shape = MaterialTheme.shapes.medium,
        ) {
            Text(
                text = "Button",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}