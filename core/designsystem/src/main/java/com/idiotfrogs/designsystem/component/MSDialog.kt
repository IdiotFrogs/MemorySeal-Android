package com.idiotfrogs.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.idiotfrogs.designsystem.component.button.MSButton
import com.idiotfrogs.designsystem.theme.MSTheme

@Composable
fun MSDialog(
    title: String,
    content: String,
    confirmText: String = "확인",
    cancelText: String = "취소",
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    confirmButtonColor: Color = MSTheme.color.primaryNormal, // 기본 초록색
    cancelButtonColor: Color = MSTheme.color.greyG1, // 기본 회색
    confirmTextColor: Color = MSTheme.color.white,
    cancelTextColor: Color = MSTheme.color.greyG4,
) {
    Dialog(onDismissRequest = onCancel) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(MSTheme.color.white)
                    .padding(20.dp)
            ) {
                MSText(
                    text = title,
                    fontSize = 20.dp,
                )
                Spacer(modifier = Modifier.height(8.dp))
                MSText(
                    text = content,
                    fontSize = 16.dp,
                    fontWeight = FontWeight.Normal
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    MSButton(
                        modifier = Modifier.weight(1f),
                        onClick = onCancel,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = cancelButtonColor
                        ),
                        contentPadding = PaddingValues(11.dp)
                    ) {
                        MSText(
                            text = cancelText,
                            fontSize = 16.dp,
                            color = cancelTextColor
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    MSButton(
                        modifier = Modifier.weight(1f),
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = confirmButtonColor
                        ),
                        contentPadding = PaddingValues(11.dp)
                    ) {
                        MSText(
                            text = confirmText,
                            fontSize = 16.dp,
                            color = confirmTextColor
                        )
                    }
                }
            }
        }
    }
}
