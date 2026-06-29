package com.idiotfrogs.designsystem.component.button

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.DisableRippleEffect
import com.idiotfrogs.designsystem.util.wavyStroke

@Composable
fun MSButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isRounded: Boolean = true,
    cornerRadius: Dp = 12.dp,
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
    wavyStrokeColor: Color? = null,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onClick: () -> Unit,
    content: @Composable (RowScope.() -> Unit),
) {
    val isPressed by interactionSource.collectIsPressedAsState()
    val cornerRadius by animateDpAsState(
        targetValue = if (isRounded) cornerRadius else 0.dp,
        animationSpec = tween(durationMillis = 300),
        label = "cornerRadiusAnimation",
    )

    DisableRippleEffect {
        Box(
            modifier = modifier.then(
                if (wavyStrokeColor != null) {
                    Modifier.wavyStroke(
                        color = wavyStrokeColor,
                        cornerRadius = cornerRadius,
                        fillColor = wavyStrokeColor,
                        contentPadding = 5.dp
                    )
                } else Modifier
            ),
            propagateMinConstraints = true,
        ) {
            Button(
                onClick = onClick,
                enabled = enabled,
                shape = RoundedCornerShape(cornerRadius),
                colors = if (isPressed) pressColors else colors,
                elevation = elevation,
                border = border,
                contentPadding = contentPadding,
                interactionSource = interactionSource,
                content = content,
            )
        }
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

@Preview
@Composable
private fun MSRectangularButtonEnabledPreview() {
    MSButton(
        modifier = Modifier.fillMaxWidth(),
        isRounded = false,
        onClick = {}
    ) {
        MSText(text = "Button")
    }
}

@Preview
@Composable
private fun MSRectangularButtonDisabledPreview() {
    MSButton(
        modifier = Modifier.fillMaxWidth(),
        enabled = false,
        isRounded = false,
        onClick = {}
    ) {
        MSText("Button")
    }
}

@Preview
@Composable
private fun MSAnimationButtonDisabledPreview() {
    var isRounded by remember { mutableStateOf(true) }

    MSButton(
        modifier = Modifier.fillMaxWidth(),
        isRounded = isRounded,
        onClick = { isRounded = !isRounded }
    ) {
        MSText("Button")
    }
}