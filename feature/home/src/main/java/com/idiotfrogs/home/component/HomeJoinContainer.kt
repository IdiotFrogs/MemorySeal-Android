package com.idiotfrogs.home.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.tooling.preview.Preview
import com.idiotfrogs.designsystem.component.MSActionContainer
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.resource.R

@Composable
fun BoxScope.HomeJoinContainer(
    isShow: Boolean,
    textFieldState: TextFieldState,
    onCancel: () -> Unit,
    onJoin: () -> Unit,
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
            title = "참여코드를 입력해 타임 티켓에 합류해 보세요!",
            hint = "예. #23923",
            textFieldState = textFieldState,
            primaryButtonText = "합류",
            onSecondaryClick = onCancel,
            onPrimaryClick = onJoin,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .focusRequester(focusRequester),
            secondaryButtonWeight = 1f,
            primaryButtonWeight = 3f,
            primaryTextColor = if (textFieldState.text.isNotEmpty()) MSTheme.color.white else MSTheme.color.greyG3,
            secondaryBorderResId = R.drawable.img_button_sub_border,
            primaryBorderResId = if (textFieldState.text.isNotEmpty()) R.drawable.img_button_primary_short_border else R.drawable.img_button_primary_light_border
        )
    }
}

@Preview
@Composable
private fun HomeJoinContainerPreview() {
    val textFieldState = rememberTextFieldState()

    Box {
        HomeJoinContainer(
            isShow = true,
            textFieldState = textFieldState,
            onCancel = { },
            onJoin = { }
        )
    }
}