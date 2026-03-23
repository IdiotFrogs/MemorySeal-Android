package com.idiotfrogs.profile.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.idiotfrogs.designsystem.component.MSText
import com.idiotfrogs.designsystem.theme.MSTheme
import com.idiotfrogs.designsystem.util.noRippleClickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileBottomSheet(
    onSelectImage: () -> Unit,
    onDefaultImage: () -> Unit,
    onDismiss: () -> Unit,
) {
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        sheetState = bottomSheetState,
        dragHandle = null,
        containerColor = MSTheme.color.white,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        onDismissRequest = onDismiss,
    ) {
        Column(
            modifier = Modifier.padding(
                top = 28.dp, bottom = 32.dp,
                start = 20.dp, end = 20.dp
            )
        ) {
            MSText(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .noRippleClickable(
                        onClick = {
                            onDismiss()
                            onSelectImage()
                        }
                    ),
                text = "앨범에서 이미지 선택",
                fontWeight = FontWeight.Bold,
                fontSize = 16.dp,
                color = MSTheme.color.greyG5
            )
            Spacer(modifier = Modifier.height(16.dp))
            val greyG1 = MSTheme.color.greyG1
            val interval = with(LocalDensity.current) { 4.dp.toPx() }
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
            ) {
                drawLine(
                    color = greyG1,
                    start = Offset(0f, size.height / 2),
                    end = Offset(size.width, size.height / 2),
                    strokeWidth = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(
                        intervals = floatArrayOf(interval, interval)
                    )
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            MSText(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .noRippleClickable(
                        onClick = {
                            onDismiss()
                            onDefaultImage()
                        }
                    ),
                text = "기본 이미지 적용",
                fontWeight = FontWeight.Bold,
                fontSize = 16.dp,
                color = MSTheme.color.greyG5
            )
        }
    }
}

@Preview
@Composable
private fun EditProfileBottomSheetPreview() {
    EditProfileBottomSheet(
        onSelectImage = {},
        onDefaultImage = {},
        onDismiss = {}
    )
}