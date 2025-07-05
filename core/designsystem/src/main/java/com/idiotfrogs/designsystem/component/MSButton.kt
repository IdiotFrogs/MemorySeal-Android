package com.idiotfrogs.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.DisableRippleEffect

@Composable
fun MSButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MSTheme.color.primaryNormal,
        disabledContainerColor = MSTheme.color.primaryLight
    ),
    pressColors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MSTheme.color.primaryDark,
        disabledContainerColor = MSTheme.color.primaryLight
    ),
    elevation: ButtonElevation? = null,
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onClick: () -> Unit,
    content: @Composable (RowScope.() -> Unit),
) {
    val isPressed by interactionSource.collectIsPressedAsState()

    DisableRippleEffect {
        Button(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            shape = RoundedCornerShape(12.dp),
            colors = if (isPressed) pressColors else colors,
            elevation = elevation,
            border = border,
            contentPadding = contentPadding,
            interactionSource = interactionSource,
            content = content
        )
    }
}

@Preview
@Composable
private fun MSButtonEnabledPreview() {
    MSButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = {}
    ) {
        MSText(text = "Button")
    }
}

@Preview
@Composable
private fun MSButtonDisabledPreview() {
    MSButton(
        modifier = Modifier.fillMaxWidth(),
        enabled = false,
        onClick = {}
    ) {
        MSText("Button")
    }
}