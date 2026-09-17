package com.idiotfrogs.profile.component

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.text.input.TextFieldState
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
import com.idiotfrogs.designsystem.component.MSActionContainer
import com.idiotfrogs.designsystem.theme.MSTheme

@Composable
fun BoxScope.ProfileWithdrawBottomSheet(
    isShow: Boolean,
    textFieldState: TextFieldState,
    onWithdraw: () -> Unit,
    onCancel: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isShow) {
        if (isShow) {
            textFieldState.setTextAndPlaceCursorAtEnd("")
            focusRequester.requestFocus()
        }
    }

    if (isShow) {
        MSActionContainer(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .focusRequester(focusRequester),
            title = "회원탈퇴",
            subtitle = "메실 회원을 탈퇴를 위해 \"회원탈퇴\"를 입력해주세요.",
            content = "티켓에 저장된 내용은 삭제되지 않습니다.",
            textFieldState = textFieldState,
            hint = "",
            primaryButtonEnabled = textFieldState.text == "회원탈퇴",
            primaryButtonText = "탈퇴",
            primaryWavyStrokeColor = if (textFieldState.text == "회원탈퇴") {
                MSTheme.color.red
            } else {
                Color(0xFFF3BBBB)
            },
            primaryPressColors = ButtonDefaults.buttonColors(
                containerColor = MSTheme.color.red,
                disabledContainerColor = Color(0xFFF3BBBB)
            ),
            primaryButtonColors = ButtonDefaults.buttonColors(
                containerColor = MSTheme.color.red,
                disabledContainerColor = Color(0xFFF3BBBB)
            ),
            secondaryWavyStrokeColor = MSTheme.color.greyG1,
            secondaryButtonText = "취소",
            onPrimaryClick = onWithdraw,
            onSecondaryClick = onCancel
        )
    }
}