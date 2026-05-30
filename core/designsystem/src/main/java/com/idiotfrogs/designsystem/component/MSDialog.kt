package com.idiotfrogs.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.idiotfrogs.designsystem.component.button.MSButton
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.wavyStroke

@Composable
fun MSDialog(
    title: String,
    content: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    confirmText: String = "확인",
    cancelText: String = "취소",
    radius: Dp = 24.dp,
    confirmButtonColor: Color = MSTheme.color.primaryNormal, // 기본 초록색
    cancelButtonColor: Color = MSTheme.color.greyG1, // 기본 회색
    confirmTextColor: Color = MSTheme.color.white,
    cancelTextColor: Color = MSTheme.color.greyG4,
) {
    Dialog(onDismissRequest = onCancel) {
        Box(modifier = modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(radius))
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
                        pressColors = ButtonDefaults.buttonColors(
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
                        pressColors = ButtonDefaults.buttonColors(
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

@Composable
fun MSTitleDialog(
    title: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    confirmText: String = "확인",
    cancelText: String = "취소",
    confirmButtonColor: Color = MSTheme.color.primaryNormal,
    cancelButtonColor: Color = MSTheme.color.greyG1,
    confirmTextColor: Color = MSTheme.color.white,
    cancelTextColor: Color = MSTheme.color.greyG4,
    content: @Composable ColumnScope.() -> Unit,
) {
    Dialog(onDismissRequest = onCancel) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .wavyStroke(
                    color = MSTheme.color.white,
                    fillColor = MSTheme.color.white,
                    contentPadding = 20.dp,
                )
        ) {
            MSText(
                text = title,
                fontSize = 20.dp,
            )
            content()
            Row(modifier = Modifier.fillMaxWidth()) {
                MSButton(
                    modifier = Modifier.weight(1f),
                    onClick = onCancel,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = cancelButtonColor
                    ),
                    pressColors = ButtonDefaults.buttonColors(
                        containerColor = cancelButtonColor
                    ),
                    wavyStrokeColor = cancelButtonColor,
                    contentPadding = PaddingValues(12.dp)
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
                    pressColors = ButtonDefaults.buttonColors(
                        containerColor = confirmButtonColor
                    ),
                    wavyStrokeColor = confirmButtonColor,
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

@Composable
fun MSTitleDialog(
    title: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    confirmText: String = "확인",
    confirmButtonColor: Color = MSTheme.color.primaryNormal,
    confirmTextColor: Color = MSTheme.color.white,
    content: @Composable ColumnScope.() -> Unit,
) {
    Dialog(onDismissRequest = onCancel) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .wavyStroke(
                    color = MSTheme.color.white,
                    fillColor = MSTheme.color.white,
                    contentPadding = 20.dp,
                )
        ) {
            MSText(
                text = title,
                fontSize = 20.dp,
            )
            content()
            MSButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = confirmButtonColor
                ),
                pressColors = ButtonDefaults.buttonColors(
                    containerColor = confirmButtonColor
                ),
                wavyStrokeColor = confirmButtonColor,
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

@Preview
@Composable
fun MSDialogPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MSTheme.color.white)
    ) {
        MSDialog(
            title = "제목",
            content = "내용",
            onCancel = {},
            onConfirm = {}
        )
    }
}