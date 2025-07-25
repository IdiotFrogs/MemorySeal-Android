package com.idiotfrogs.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.idiotfrogs.designsystem.component.button.MSButton
import com.idiotfrogs.designsystem.theme.MSTheme

@Composable
fun MSDialog(
    isShow: Boolean,
    title: String,
    content: String,
    primaryText: String,
    secondaryText: String,
    onDismiss: () -> Unit,
    onAction: () -> Unit,
    properties: DialogProperties = DialogProperties(),
) {
    if (isShow) {
        Dialog(
            properties = properties,
            onDismissRequest = onDismiss
        ) {
            Column(
                modifier = Modifier
                    .background(
                        color = MSTheme.color.white,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(top = 28.dp, start = 24.dp, end = 24.dp, bottom = 24.dp)
            ) {
                MSText(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.dp,
                    lineHeight = (1.3).em,
                    color = MSTheme.color.black
                )
                Spacer(modifier = Modifier.height(8.dp))
                MSText(
                    text = content,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.dp,
                    lineHeight = (1.5).em,
                    color = MSTheme.color.black
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MSButton(
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MSTheme.color.greyG1,
                            disabledContainerColor = MSTheme.color.greyG2
                        ),
                        pressColors = ButtonDefaults.buttonColors(
                            containerColor = MSTheme.color.greyG2,
                            disabledContainerColor = MSTheme.color.greyG2
                        ),
                        contentPadding = PaddingValues(vertical = 11.dp),
                        onClick = onDismiss
                    ) {
                        MSText(
                            text = secondaryText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.dp,
                            color = MSTheme.color.greyG4
                        )
                    }
                    MSButton(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 11.dp),
                        onClick = onAction
                    ) {
                        MSText(
                            text = primaryText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.dp,
                            color = MSTheme.color.white
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun MSDialogPreview() {
    MSDialog(
        isShow = true,
        title = "로그아웃",
        content = "메실에서 로그아웃 하시겠습니까?",
        primaryText = "로그아웃",
        secondaryText = "유지",
        onDismiss = { },
        onAction = { }
    )
}