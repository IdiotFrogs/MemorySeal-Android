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
import com.idiotfrogs.resource.R

@Composable
fun BoxScope.ManagementDeleteContainer(
    isShow: Boolean,
    textFieldState: TextFieldState,
    onCancel: () -> Unit,
    onDelete: () -> Unit,
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
            title = "삭제를 위해 티켓 이름”티켓 이름”을 입력해주세요.",
            hint = "”티켓 이름”",
            textFieldState = textFieldState,
            primaryButtonText = "삭제",
            onSecondaryClick = onCancel,
            onPrimaryClick = onDelete,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .focusRequester(focusRequester),
            primaryButtonColors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFDA1B1B),
                disabledContainerColor = Color(0x33DA1B1B),  // 20% 투명도
            ),
            primaryPressColors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFDA1B1B),
                disabledContainerColor = Color(0x33DA1B1B),  // 20% 투명도
            ),
            primaryTextColor = if (textFieldState.text.isNotEmpty()) MSTheme.color.white else Color(0x59DA1B1B),  // 35% 투명도
            secondaryBorderResId = R.drawable.img_button_sub_border,
            primaryBorderResId = if (textFieldState.text.isNotEmpty()) R.drawable.img_button_primary_border else R.drawable.img_button_primary_border,  // TODO 추 후 UI에 맞는 border로 변경 필요 (작업시점 디자인 X)
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
            onCancel = { },
            onDelete = { }
        )
    }
}