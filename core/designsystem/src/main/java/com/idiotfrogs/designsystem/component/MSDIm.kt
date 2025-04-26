package com.idiotfrogs.designsystem.component

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun MSDim(
    visible: Boolean,
    onDismiss: () -> Unit,
    color: Color = Color(0x3D444444),
    animationSpec: FiniteAnimationSpec<Float> = tween(200)
) {
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = animationSpec,
        label = "dim alpha"
    )

    if (alpha > 0f) {
        val interactionSource = remember { MutableInteractionSource() }

        Box(
            Modifier
                .fillMaxSize()
                .background(color.copy(alpha = color.alpha * alpha))
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onDismiss
                )
        )
    }
}


@Preview(showBackground = true)
@Composable
fun MSDimPreview() {
    var visible by remember { mutableStateOf(true) }
    MSDim(
        visible = visible,
        onDismiss = { visible = !visible }
    )
}