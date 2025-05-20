package com.idiotfrogs.designsystem.component

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Text
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
import com.idiotfrogs.designsystem.util.rememberKeyboardVisibility
import com.idiotfrogs.designsystem.util.toSp
import com.idiotfrogs.resource.pretendard

@Composable
fun MSTextField(
    modifier: Modifier = Modifier,
    textFieldState: TextFieldState,
    hint: String,
    enabled: Boolean = true,
    readOnly: Boolean = false,
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
    interactionSource: MutableInteractionSource? = null,
    cursorBrush: Brush = SolidColor(Color.Black),
    outputTransformation: OutputTransformation? = null,
    scrollState: ScrollState = rememberScrollState()
) {
    val isShowKeyboard = rememberKeyboardVisibility()
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
                    .heightIn(48.dp)
                    .border(
                        width = 1.dp,
                        color = when {
                            (enabled && !isShowKeyboard) || !enabled -> MSTheme.color.greyG2
                            else -> MSTheme.color.greyG5
                        },
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 11.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (textFieldState.text.isEmpty()) {
                    Text(
                        text = hint,
                        color = MSTheme.color.greyG3,
                        fontSize = 16.dp.toSp(),
                        fontWeight = FontWeight.Normal,
                        lineHeight = 16.dp.toSp() * 1.6,
                    )
                }
                innerTextField()
            }
        },
        scrollState = scrollState
    )
}

@Preview
@Composable
private fun MSTextFieldPreview() {
    val textFieldState = rememberTextFieldState()
    MSTextField(
        modifier = Modifier.fillMaxWidth(),
        hint = "별명을 입력해주세요.",
        textFieldState = textFieldState
    )
}

@Preview
@Composable
private fun MSTextFieldDisabledPreview() {
    val textFieldState = rememberTextFieldState()
    MSTextField(
        modifier = Modifier.fillMaxWidth(),
        enabled = false,
        hint = "별명을 입력해주세요.",
        textFieldState = textFieldState
    )
}