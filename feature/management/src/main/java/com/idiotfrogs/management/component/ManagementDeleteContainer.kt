package com.idiotfrogs.management.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.idiotfrogs.designsystem.component.MSActionContainer
import com.idiotfrogs.designsystem.theme.MSTheme

@Composable
fun BoxScope.ManagementDeleteContainer(
    isShow: Boolean,
    textFieldState: TextFieldState,
    capsuleTitle: String,
    onCancel: () -> Unit,
    onDelete: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val enabled = textFieldState.text.toString() == capsuleTitle

    LaunchedEffect(isShow) {
        if (isShow) {
            textFieldState.setTextAndPlaceCursorAtEnd("")
            focusRequester.requestFocus()
        }
    }

    if (isShow) {
        MSActionContainer(
            title = "삭제를 위해 티켓 이름\n\"$capsuleTitle\"을/를 입력해주세요.",
            hint = capsuleTitle,
            textFieldState = textFieldState,
            primaryButtonText = "삭제",
            onSecondaryClick = onCancel,
            onPrimaryClick = onDelete,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .focusRequester(focusRequester),
            primaryButtonEnabled = enabled,
            focusedBorderColor = MSTheme.color.greyG1,
            primaryButtonColors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFED1E1E),
                disabledContainerColor = Color(0xFFF3BBBB),
            ),
            primaryPressColors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFED1E1E),
                disabledContainerColor = Color(0xFFF3BBBB),
            ),
            primaryTextColor = if (enabled) MSTheme.color.white else Color(0x59DA1B1B),  // 35% 투명도
            secondaryWavyStrokeColor = MSTheme.color.greyG1,
            primaryWavyStrokeColor = if (enabled) Color(0xFFED1E1E) else Color(0xFFF3BBBB),
        )
    }
}

@Preview
@Composable
private fun ManagementDeleteContainerPreview() {
    val textFieldState = rememberTextFieldState()

    Box {
        ManagementDeleteContainer(
            isShow = true,
            textFieldState = textFieldState,
            capsuleTitle = "티켓 Title",
            onCancel = { },
            onDelete = { }
        )
    }
}
