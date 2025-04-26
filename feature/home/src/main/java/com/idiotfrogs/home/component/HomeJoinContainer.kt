package com.idiotfrogs.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.component.MSButton
import com.idiotfrogs.designsystem.component.MSTextField
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.toSp
import com.idiotfrogs.resource.pretendard

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
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .focusRequester(focusRequester)
                .fillMaxWidth()
                .background(
                    color = MSTheme.color.white,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                )
                .padding(horizontal = 20.dp, vertical = 24.dp)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "참여코드를 입력해 타임 티켓에 합류해 보세요!",
                fontWeight = FontWeight.Bold,
                fontSize = 16.dp.toSp(),
                color = MSTheme.color.black
            )
            Spacer(modifier = Modifier.height(16.dp))
            MSTextField(
                modifier = Modifier.fillMaxWidth(),
                textFieldState = textFieldState,
                hint = "예. #23923"
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MSButton(
                    modifier = Modifier.size(width = 80.dp, height = 48.dp),
                    onClick = onCancel,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MSTheme.color.greyG1,
                        disabledContainerColor = MSTheme.color.greyG1
                    ),
                    pressColors = ButtonDefaults.buttonColors(
                        containerColor = MSTheme.color.greyG1,
                        disabledContainerColor = MSTheme.color.greyG1
                    )
                ) {
                    Text(
                        text = "취소",
                        fontFamily = pretendard,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.dp.toSp(),
                        color = MSTheme.color.greyG5
                    )
                }
                MSButton(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    enabled = textFieldState.text.isNotEmpty(),
                    onClick = onJoin,
                ) {
                    Text(
                        text = "합류",
                        fontFamily = pretendard,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.dp.toSp(),
                        color = MSTheme.color.white
                    )
                }
            }
        }
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