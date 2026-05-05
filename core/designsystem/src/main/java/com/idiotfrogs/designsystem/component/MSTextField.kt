package com.idiotfrogs.designsystem.component

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.rememberFocusState
import com.idiotfrogs.designsystem.util.toSp
import com.idiotfrogs.designsystem.util.wavyStroke
import com.idiotfrogs.resource.pretendard

@Composable
fun MSTextField(
    modifier: Modifier = Modifier,
    hint: String,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textFieldState: TextFieldState = rememberTextFieldState(),
    focusState: Pair<MutableInteractionSource, Boolean> = rememberFocusState(),
    inputTransformation: InputTransformation? = null,
    textStyle: TextStyle = TextStyle(
        color = MSTheme.color.greyG5,
        fontSize = 16.dp.toSp(),
        fontWeight = FontWeight.Normal,
        fontFamily = pretendard,
        lineHeight = 16.dp.toSp() * 1.6,
    ),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onKeyboardAction: KeyboardActionHandler? = null,
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.SingleLine,
    onTextLayout: (Density.(getResult: () -> TextLayoutResult?) -> Unit)? = null,
    cursorBrush: Brush = SolidColor(Color.Black),
    outputTransformation: OutputTransformation? = null,
    scrollState: ScrollState = rememberScrollState()
) {
    val (interactionSource, isFocused) = focusState

    BasicTextField(
        state = textFieldState,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        inputTransformation = inputTransformation,
        textStyle = textStyle,
        keyboardOptions = keyboardOptions,
        onKeyboardAction = onKeyboardAction,
        lineLimits = lineLimits,
        onTextLayout = onTextLayout,
        interactionSource = interactionSource,
        cursorBrush = cursorBrush,
        outputTransformation = outputTransformation,
        decorator = { innerTextField ->
            Box(
                modifier = Modifier
                    .heightIn(54.dp)  // UI : 48dp + 테두리 3+3 = 6dp => 54dp
                    .fillMaxWidth()
                    .wavyStroke(
                        color = if (isFocused) MSTheme.color.primaryNormal else MSTheme.color.greyG1,
                        amplitude = 1.5.dp,
                        spacing = 5.dp,
                    )
            ) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(15.dp), // UI : 12dp + 테두리 3dp => 15dp
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (textFieldState.text.isEmpty()) {
                        MSText(
                            text = hint,
                            color = MSTheme.color.greyG3,
                            fontSize = 16.dp,
                            fontWeight = FontWeight.Normal,
                            lineHeight = 16.dp.toSp() * 1.6,
                        )
                    }
                    innerTextField()
                }
            }
        },
        scrollState = scrollState
    )
}

@Preview
@Composable
private fun MSTextFieldPreview() {
    MSTextField(
        modifier = Modifier.fillMaxWidth(),
        hint = "별명을 입력해주세요.",
    )
}

@Preview
@Composable
private fun MSTextFieldDisabledPreview() {
    MSTextField(
        modifier = Modifier.fillMaxWidth(),
        enabled = false,
        hint = "별명을 입력해주세요.",
    )
}